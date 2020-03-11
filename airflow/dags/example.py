import logging
from datetime import datetime, timedelta

from airflow import DAG
from airflow.models import Variable
from airflow.operators.latest_only_operator import LatestOnlyOperator
from airflow.operators.docker_operator import DockerOperator
from airflow.operators.bash_operator import BashOperator


default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2020,2,1),
    'email': ['airflow@example.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 0,
    'retry_delay': timedelta(minutes=5)
}

dag = DAG('Example_DAG', schedule_interval=timedelta(days=7), default_args=default_args)

with dag:

    latest_only = LatestOnlyOperator(task_id='latest_only')

    secure_workspace = DockerOperator(
                           task_id='set_workspace',
                           image='bio_dwh2:latest',
                           api_version='auto',
                           auto_remove=True,
                           volumes=['/home/jgrebe/Uni/fork/BioDWH2/airflow/workspace:/home/workspace'],
                           command='-c /home/workspace',
                           docker_url="unix://var/run/docker.sock",
                           network_mode="bridge"
    )

    data_source = Variable.get("dataSourceIds")

    set_workspace_config = BashOperator(
        task_id="configure_workspace",
        bash_command=f'''sed -i 's/"dataSource.*]/"dataSourceIds" : {data_source}/' /home/workspace/config.json''',
        dag=dag,
    )

    latest_only >> secure_workspace >> set_workspace_config


    for i in data_source.strip('][').split(', '):
        dynamic_task = DockerOperator(
                            task_id=f'''generate_from_source_{i.replace('"', '')}''',
                            image='bio_dwh2:latest',
                            api_version='auto',
                            auto_remove=True,
                            volumes=['/home/jgrebe/Uni/fork/BioDWH2/airflow/workspace:/home/workspace'],
                            command='-u /home/workspace',
                            docker_url="unix://var/run/docker.sock",
                            network_mode="bridge"
                        )
        set_workspace_config >> dynamic_task



