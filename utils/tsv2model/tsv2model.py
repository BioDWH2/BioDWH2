import os
import csv
import sys


def safe_index_of(text: str, search_term: str, start: int) -> int:
    try:
        return text.index(search_term, start)
    except ValueError:
        return -1


def snake_case_to_camel_case(text: str) -> str:
    text = snake_case_to_pascal_case(text)
    return text[0].lower() + text[1:]


def snake_case_to_pascal_case(text: str) -> str:
    text = text[0].upper() + text[1:]
    index = safe_index_of(text, '_', 0)
    while index != -1:
        text = text[0:index] + text[index + 1].upper() + text[(index + 2):]
        index = safe_index_of(text, '_', index + 1)
    return text


if __name__ == '__main__':
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    package = sys.argv[3]
    if os.path.isdir(input_path):
        files = [os.path.join(input_path, f) for f in os.listdir(input_path) if
                 os.path.isfile(os.path.join(input_path, f)) and f.endswith('.tsv')]
    else:
        files = [input_path]
    for file_path in files:
        file_name = os.path.basename(file_path)
        print('Generating model for "%s"...' % file_name)
        with open(file_path, 'r', encoding='utf-8') as f:
            reader = csv.reader(f, delimiter='\t', quotechar='"')
            header_row = next(reader)
        output_file = snake_case_to_pascal_case(file_name.replace('.tsv', '.java'))
        output_file_path = os.path.join(output_path, output_file)
        if not os.path.exists(output_file_path):
            with open(output_file_path, 'w', encoding='utf-8', newline='') as f:
                f.write('package %s;\n' % package)
                f.write('\n')
                f.write('import com.fasterxml.jackson.annotation.JsonProperty;\n')
                f.write('import com.fasterxml.jackson.annotation.JsonPropertyOrder;\n')
                f.write('\n')
                f.write('@JsonPropertyOrder({%s})\n' % ('"' + '", "'.join(header_row) + '"'))
                f.write('public class %s {\n' % output_file.replace('.java', ''))
                for column_name in header_row:
                    f.write('    @JsonProperty("%s")\n' % column_name)
                    f.write('    public String %s;\n' % snake_case_to_camel_case(column_name))
                f.write('}\n')
