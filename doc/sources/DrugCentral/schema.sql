SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.tdkey2tc DROP CONSTRAINT IF EXISTS tdgo2tc_2_tc;
ALTER TABLE IF EXISTS ONLY public.tdgo2tc DROP CONSTRAINT IF EXISTS tdgo2tc_2_tc;
ALTER TABLE IF EXISTS ONLY public.tdkey2tc DROP CONSTRAINT IF EXISTS tdgo2tc_2_kw;
ALTER TABLE IF EXISTS ONLY public.tdgo2tc DROP CONSTRAINT IF EXISTS tdgo2tc_2_go;
ALTER TABLE IF EXISTS ONLY public.td2tc DROP CONSTRAINT IF EXISTS td2tc_2_td;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS synonym_2_struct;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS synonym_2_parent;
ALTER TABLE IF EXISTS ONLY public.structure_type DROP CONSTRAINT IF EXISTS structure_type_2_type;
ALTER TABLE IF EXISTS ONLY public.structure_type DROP CONSTRAINT IF EXISTS structure_type_2_struct;
ALTER TABLE IF EXISTS ONLY public.structures DROP CONSTRAINT IF EXISTS struct_2_stem;
ALTER TABLE IF EXISTS ONLY public.struct2obprod DROP CONSTRAINT IF EXISTS struct_2_obprod;
ALTER TABLE IF EXISTS ONLY public.struct2parent DROP CONSTRAINT IF EXISTS struct2parent_2_struct;
ALTER TABLE IF EXISTS ONLY public.struct2parent DROP CONSTRAINT IF EXISTS struct2parent_2_parent;
ALTER TABLE IF EXISTS ONLY public.struct2drgclass DROP CONSTRAINT IF EXISTS struct2drgclass_2_struct;
ALTER TABLE IF EXISTS ONLY public.struct2drgclass DROP CONSTRAINT IF EXISTS struct2drgclass_2_drgclass;
ALTER TABLE IF EXISTS ONLY public.struct2atc DROP CONSTRAINT IF EXISTS struct2atc_2_struct;
ALTER TABLE IF EXISTS ONLY public.struct2atc DROP CONSTRAINT IF EXISTS struct2atc_2_atc;
ALTER TABLE IF EXISTS ONLY public.section DROP CONSTRAINT IF EXISTS section_2_label;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS reference_2_reftype;
ALTER TABLE IF EXISTS ONLY public.prd2label DROP CONSTRAINT IF EXISTS prd_2_prd;
ALTER TABLE IF EXISTS ONLY public.prd2label DROP CONSTRAINT IF EXISTS prd_2_label;
ALTER TABLE IF EXISTS ONLY public.pka DROP CONSTRAINT IF EXISTS pka_2_struct;
ALTER TABLE IF EXISTS ONLY public.pharma_class DROP CONSTRAINT IF EXISTS pharma_class_2_struct;
ALTER TABLE IF EXISTS ONLY public.pdb DROP CONSTRAINT IF EXISTS pdb_2_struct;
ALTER TABLE IF EXISTS ONLY public.omop_relationship DROP CONSTRAINT IF EXISTS omop_relationship_struct_id_fkey;
ALTER TABLE IF EXISTS ONLY public.struct2obprod DROP CONSTRAINT IF EXISTS obprod_2_struct;
ALTER TABLE IF EXISTS ONLY public.ob_patent DROP CONSTRAINT IF EXISTS obpat_2_usecode;
ALTER TABLE IF EXISTS ONLY public.ob_exclusivity DROP CONSTRAINT IF EXISTS obexcl_2_exclcode;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS moa_2_ref;
ALTER TABLE IF EXISTS ONLY public.identifier DROP CONSTRAINT IF EXISTS identifier_2_struct;
ALTER TABLE IF EXISTS ONLY public.identifier DROP CONSTRAINT IF EXISTS identifier_2_idtype;
ALTER TABLE IF EXISTS ONLY public.faers DROP CONSTRAINT IF EXISTS faers_2_struct;
ALTER TABLE IF EXISTS ONLY public.doid_xref DROP CONSTRAINT IF EXISTS doid_xref_2_doid;
ALTER TABLE IF EXISTS ONLY public.ddi DROP CONSTRAINT IF EXISTS ddi_2_drug_class2;
ALTER TABLE IF EXISTS ONLY public.ddi DROP CONSTRAINT IF EXISTS ddi_2_drug_class1;
ALTER TABLE IF EXISTS ONLY public.ddi DROP CONSTRAINT IF EXISTS ddi_2_ddi_risk;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS bioact_2_ref;
ALTER TABLE IF EXISTS ONLY public.atc_ddd DROP CONSTRAINT IF EXISTS atc_ddd_2_struct;
ALTER TABLE IF EXISTS ONLY public.approval DROP CONSTRAINT IF EXISTS approval_2_type;
ALTER TABLE IF EXISTS ONLY public.approval DROP CONSTRAINT IF EXISTS approval_2_struct;
ALTER TABLE IF EXISTS ONLY public.active_ingredient DROP CONSTRAINT IF EXISTS active_ingredient_ndc_product_code_fkey;
ALTER TABLE IF EXISTS ONLY public.active_ingredient DROP CONSTRAINT IF EXISTS active_ingredient_2_struct;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS act_table_full_2_target_dict;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS act_table_full_2_target_class;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS act_table_full_2_struct;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS act_table_full_2_act_type;
DROP INDEX IF EXISTS public.xref_source_idx;
DROP INDEX IF EXISTS public.sql121023161238850;
DROP INDEX IF EXISTS public.sql120925163230921;
DROP INDEX IF EXISTS public.sql120925163230920;
DROP INDEX IF EXISTS public.sql120523120921131;
DROP INDEX IF EXISTS public.sql120418113711390;
DROP INDEX IF EXISTS public.sql120412004634310;
DROP INDEX IF EXISTS public.sql120412004620930;
DROP INDEX IF EXISTS public.sql120404123859152;
DROP INDEX IF EXISTS public.sql120404123658531;
DROP INDEX IF EXISTS public.sql100501183943930;
DROP INDEX IF EXISTS public.ob_product_prodno_idx;
DROP INDEX IF EXISTS public.ob_product_appno_idx;
DROP INDEX IF EXISTS public.ob_patent_prodno_idx;
DROP INDEX IF EXISTS public.ob_patent_applno_idx;
DROP INDEX IF EXISTS public.index_ijc_connect_items;
DROP INDEX IF EXISTS public."SQL0000000019-560c80d5-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000017-d46b80cd-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000016-eaa980c4-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000015-b278c0c3-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000014-4fe880b5-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000013-67bb80b4-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000006-48928085-0182-8659-7929-00007c5ff800";
DROP INDEX IF EXISTS public."SQL0000000000-fb160050-0182-81c2-aa9c-00001f0585e8";
DROP INDEX IF EXISTS public."SQL0000000000-a352c053-0182-81bb-5234-00001f0067d8";
DROP INDEX IF EXISTS public."SQL0000000000-6302404f-0182-82a6-63b4-00007c800000";
ALTER TABLE IF EXISTS ONLY public.structures DROP CONSTRAINT IF EXISTS uniq_structures_id;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS syn_lname_uq;
ALTER TABLE IF EXISTS ONLY public.property_type DROP CONSTRAINT IF EXISTS symbol;
ALTER TABLE IF EXISTS ONLY public.structure_type DROP CONSTRAINT IF EXISTS struct_id_type_uq;
ALTER TABLE IF EXISTS ONLY public.struct2atc DROP CONSTRAINT IF EXISTS struct2atc_id_key;
ALTER TABLE IF EXISTS ONLY public.faers_female DROP CONSTRAINT IF EXISTS sql200913054200240;
ALTER TABLE IF EXISTS ONLY public.faers_male DROP CONSTRAINT IF EXISTS sql200913054154500;
ALTER TABLE IF EXISTS ONLY public.lincs_signature DROP CONSTRAINT IF EXISTS sql180509154922471;
ALTER TABLE IF EXISTS ONLY public.lincs_signature DROP CONSTRAINT IF EXISTS sql180509154922470;
ALTER TABLE IF EXISTS ONLY public.faers DROP CONSTRAINT IF EXISTS sql180422234202640;
ALTER TABLE IF EXISTS ONLY public.pka DROP CONSTRAINT IF EXISTS sql180408153602180;
ALTER TABLE IF EXISTS ONLY public.ob_patent_use_code DROP CONSTRAINT IF EXISTS sql161127133626130;
ALTER TABLE IF EXISTS ONLY public.ob_exclusivity_code DROP CONSTRAINT IF EXISTS sql161127133109120;
ALTER TABLE IF EXISTS ONLY public.ob_patent DROP CONSTRAINT IF EXISTS sql161127120038110;
ALTER TABLE IF EXISTS ONLY public.ob_exclusivity DROP CONSTRAINT IF EXISTS sql161127115514940;
ALTER TABLE IF EXISTS ONLY public.struct2obprod DROP CONSTRAINT IF EXISTS sql161126154450310;
ALTER TABLE IF EXISTS ONLY public.ob_product DROP CONSTRAINT IF EXISTS sql161126154442460;
ALTER TABLE IF EXISTS ONLY public.dbversion DROP CONSTRAINT IF EXISTS sql160415165555160;
ALTER TABLE IF EXISTS ONLY public.act_table_full DROP CONSTRAINT IF EXISTS sql160219095125231;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS sql150826201920375;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS sql150826201920374;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS sql150826201920371;
ALTER TABLE IF EXISTS ONLY public.synonyms DROP CONSTRAINT IF EXISTS sql150826201920370;
ALTER TABLE IF EXISTS ONLY public.pharma_class DROP CONSTRAINT IF EXISTS sql150603161251841;
ALTER TABLE IF EXISTS ONLY public.pharma_class DROP CONSTRAINT IF EXISTS sql150603161251830;
ALTER TABLE IF EXISTS ONLY public.struct2parent DROP CONSTRAINT IF EXISTS sql150529131801300;
ALTER TABLE IF EXISTS ONLY public.parentmol DROP CONSTRAINT IF EXISTS sql150523184644290;
ALTER TABLE IF EXISTS ONLY public.parentmol DROP CONSTRAINT IF EXISTS sql150523184621160;
ALTER TABLE IF EXISTS ONLY public.parentmol DROP CONSTRAINT IF EXISTS sql150523184351770;
ALTER TABLE IF EXISTS ONLY public.doid_xref DROP CONSTRAINT IF EXISTS sql150426005334632;
ALTER TABLE IF EXISTS ONLY public.doid_xref DROP CONSTRAINT IF EXISTS sql150426005334630;
ALTER TABLE IF EXISTS ONLY public.doid DROP CONSTRAINT IF EXISTS sql150425232401221;
ALTER TABLE IF EXISTS ONLY public.doid DROP CONSTRAINT IF EXISTS sql150425232401220;
ALTER TABLE IF EXISTS ONLY public.pdb DROP CONSTRAINT IF EXISTS sql150123095054722;
ALTER TABLE IF EXISTS ONLY public.pdb DROP CONSTRAINT IF EXISTS sql150123095054720;
ALTER TABLE IF EXISTS ONLY public.tdgo2tc DROP CONSTRAINT IF EXISTS sql141211235052890;
ALTER TABLE IF EXISTS ONLY public.target_go DROP CONSTRAINT IF EXISTS sql141211234759820;
ALTER TABLE IF EXISTS ONLY public.tdkey2tc DROP CONSTRAINT IF EXISTS sql141211195643960;
ALTER TABLE IF EXISTS ONLY public.target_keyword DROP CONSTRAINT IF EXISTS sql141211160454700;
ALTER TABLE IF EXISTS ONLY public.action_type DROP CONSTRAINT IF EXISTS sql141210123613761;
ALTER TABLE IF EXISTS ONLY public.action_type DROP CONSTRAINT IF EXISTS sql141210123613760;
ALTER TABLE IF EXISTS ONLY public.td2tc DROP CONSTRAINT IF EXISTS sql141205191436250;
ALTER TABLE IF EXISTS ONLY public.target_dictionary DROP CONSTRAINT IF EXISTS sql141205191111190;
ALTER TABLE IF EXISTS ONLY public.protein_type DROP CONSTRAINT IF EXISTS sql141203213631731;
ALTER TABLE IF EXISTS ONLY public.protein_type DROP CONSTRAINT IF EXISTS sql141203213631730;
ALTER TABLE IF EXISTS ONLY public.target_component DROP CONSTRAINT IF EXISTS sql141203005155671;
ALTER TABLE IF EXISTS ONLY public.target_component DROP CONSTRAINT IF EXISTS sql141203005155670;
ALTER TABLE IF EXISTS ONLY public.target_component DROP CONSTRAINT IF EXISTS sql141203005155660;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS sql141129210458574;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS sql141129210458573;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS sql141129210458572;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS sql141129210458571;
ALTER TABLE IF EXISTS ONLY public.reference DROP CONSTRAINT IF EXISTS sql141129210458570;
ALTER TABLE IF EXISTS ONLY public.approval DROP CONSTRAINT IF EXISTS sql141031231617263;
ALTER TABLE IF EXISTS ONLY public.approval DROP CONSTRAINT IF EXISTS sql141031231617260;
ALTER TABLE IF EXISTS ONLY public.approval_type DROP CONSTRAINT IF EXISTS sql141031231522061;
ALTER TABLE IF EXISTS ONLY public.approval_type DROP CONSTRAINT IF EXISTS sql141031231522060;
ALTER TABLE IF EXISTS ONLY public.struct_type_def DROP CONSTRAINT IF EXISTS sql141016095933601;
ALTER TABLE IF EXISTS ONLY public.struct_type_def DROP CONSTRAINT IF EXISTS sql141016095933600;
ALTER TABLE IF EXISTS ONLY public.struct2drgclass DROP CONSTRAINT IF EXISTS sql140608153701271;
ALTER TABLE IF EXISTS ONLY public.struct2drgclass DROP CONSTRAINT IF EXISTS sql140608153701270;
ALTER TABLE IF EXISTS ONLY public.identifier DROP CONSTRAINT IF EXISTS sql140607225949710;
ALTER TABLE IF EXISTS ONLY public.id_type DROP CONSTRAINT IF EXISTS sql140607012055121;
ALTER TABLE IF EXISTS ONLY public.id_type DROP CONSTRAINT IF EXISTS sql140607012055120;
ALTER TABLE IF EXISTS ONLY public.atc_ddd DROP CONSTRAINT IF EXISTS sql140512172435200;
ALTER TABLE IF EXISTS ONLY public.ddi DROP CONSTRAINT IF EXISTS sql140411131553960;
ALTER TABLE IF EXISTS ONLY public.ddi_risk DROP CONSTRAINT IF EXISTS sql140411131027290;
ALTER TABLE IF EXISTS ONLY public.attr_type DROP CONSTRAINT IF EXISTS sql140410123913681;
ALTER TABLE IF EXISTS ONLY public.attr_type DROP CONSTRAINT IF EXISTS sql140410123913680;
ALTER TABLE IF EXISTS ONLY public.drug_class DROP CONSTRAINT IF EXISTS sql140409195222371;
ALTER TABLE IF EXISTS ONLY public.drug_class DROP CONSTRAINT IF EXISTS sql140409195222370;
ALTER TABLE IF EXISTS ONLY public.ref_type DROP CONSTRAINT IF EXISTS sql140401160903571;
ALTER TABLE IF EXISTS ONLY public.ref_type DROP CONSTRAINT IF EXISTS sql140401160903570;
ALTER TABLE IF EXISTS ONLY public.inn_stem DROP CONSTRAINT IF EXISTS sql140212001438201;
ALTER TABLE IF EXISTS ONLY public.inn_stem DROP CONSTRAINT IF EXISTS sql140212001438200;
ALTER TABLE IF EXISTS ONLY public.prd2label DROP CONSTRAINT IF EXISTS sql130919144750040;
ALTER TABLE IF EXISTS ONLY public.target_class DROP CONSTRAINT IF EXISTS sql130710170452610;
ALTER TABLE IF EXISTS ONLY public.atc DROP CONSTRAINT IF EXISTS sql130424125636181;
ALTER TABLE IF EXISTS ONLY public.atc DROP CONSTRAINT IF EXISTS sql130424125636180;
ALTER TABLE IF EXISTS ONLY public.omop_relationship DROP CONSTRAINT IF EXISTS sql121023161238820;
ALTER TABLE IF EXISTS ONLY public.structure_type DROP CONSTRAINT IF EXISTS sql120925163230900;
ALTER TABLE IF EXISTS ONLY public.struct2atc DROP CONSTRAINT IF EXISTS sql120523120921130;
ALTER TABLE IF EXISTS ONLY public.product DROP CONSTRAINT IF EXISTS sql120412001426700;
ALTER TABLE IF EXISTS ONLY public.active_ingredient DROP CONSTRAINT IF EXISTS sql120404123859150;
ALTER TABLE IF EXISTS ONLY public.section DROP CONSTRAINT IF EXISTS sql120404123658530;
ALTER TABLE IF EXISTS ONLY public.label DROP CONSTRAINT IF EXISTS sql120404123647220;
ALTER TABLE IF EXISTS ONLY public.data_source DROP CONSTRAINT IF EXISTS sql100517171435170;
ALTER TABLE IF EXISTS ONLY public.structures DROP CONSTRAINT IF EXISTS sql100501171817150;
ALTER TABLE IF EXISTS ONLY public.product DROP CONSTRAINT IF EXISTS prd_ndc_uniq;
ALTER TABLE IF EXISTS ONLY public.prd2label DROP CONSTRAINT IF EXISTS prd2label_id_key;
ALTER TABLE IF EXISTS ONLY public.ijc_connect_structures DROP CONSTRAINT IF EXISTS pk_ijc_connect_structures;
ALTER TABLE IF EXISTS ONLY public.ijc_connect_items DROP CONSTRAINT IF EXISTS pk_ijc_connect_items;
ALTER TABLE IF EXISTS ONLY public.omop_relationship DROP CONSTRAINT IF EXISTS omoprel_struct_concept_uq;
ALTER TABLE IF EXISTS ONLY public.identifier DROP CONSTRAINT IF EXISTS identifier_unique;
ALTER TABLE IF EXISTS ONLY public.ddi DROP CONSTRAINT IF EXISTS ddi_tuple_uq;
ALTER TABLE IF EXISTS ONLY public.ddi_risk DROP CONSTRAINT IF EXISTS ddi_risk_uq;
ALTER TABLE IF EXISTS ONLY public.structures DROP CONSTRAINT IF EXISTS cas_reg_no_uq;
ALTER TABLE IF EXISTS ONLY public.vetomop DROP CONSTRAINT IF EXISTS "SQL0000000018-6dd780d4-0182-8659-7929-00007c5ff800";
ALTER TABLE IF EXISTS ONLY public.vetprod DROP CONSTRAINT IF EXISTS "SQL0000000012-af8ec0b3-0182-8659-7929-00007c5ff800";
ALTER TABLE IF EXISTS ONLY public.vetprod_type DROP CONSTRAINT IF EXISTS "SQL0000000007-f0b3c086-0182-8659-7929-00007c5ff800";
ALTER TABLE IF EXISTS public.vetprod ALTER COLUMN prodid DROP DEFAULT;
ALTER TABLE IF EXISTS public.vetomop ALTER COLUMN omopid DROP DEFAULT;
ALTER TABLE IF EXISTS public.tdkey2tc ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.tdgo2tc ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.target_dictionary ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.target_component ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.target_class ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.synonyms ALTER COLUMN syn_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.structures ALTER COLUMN cd_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.structure_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.struct_type_def ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.struct2drgclass ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.struct2atc ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.section ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.reference ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ref_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.protein_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.property_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.property ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.prd2label ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.pka ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.pharma_class ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.pdb ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.parentmol ALTER COLUMN cd_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.omop_relationship ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ob_product ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ob_patent ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ob_exclusivity ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.lincs_signature ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.inn_stem ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.identifier ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.id_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.faers_ped ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.faers_male ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.faers_ger ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.faers_female ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.faers ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.drug_class ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.doid_xref ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ddi_risk ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.ddi ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.attr_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.atc_ddd ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.atc ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.approval_type ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.approval ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.active_ingredient ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.action_type ALTER COLUMN id DROP DEFAULT;
DROP TABLE IF EXISTS public.vettype;
DROP TABLE IF EXISTS public.vetprod_type;
DROP SEQUENCE IF EXISTS public.vetprod_prodid_seq;
DROP TABLE IF EXISTS public.vetprod2struct;
DROP TABLE IF EXISTS public.vetprod;
DROP SEQUENCE IF EXISTS public.vetomop_omopid_seq;
DROP TABLE IF EXISTS public.vetomop;
DROP SEQUENCE IF EXISTS public.tdkey2tc_id_seq;
DROP TABLE IF EXISTS public.tdkey2tc;
DROP SEQUENCE IF EXISTS public.tdgo2tc_id_seq;
DROP TABLE IF EXISTS public.tdgo2tc;
DROP TABLE IF EXISTS public.td2tc;
DROP TABLE IF EXISTS public.target_keyword;
DROP TABLE IF EXISTS public.target_go;
DROP SEQUENCE IF EXISTS public.target_dictionary_id_seq;
DROP TABLE IF EXISTS public.target_dictionary;
DROP SEQUENCE IF EXISTS public.target_component_id_seq;
DROP TABLE IF EXISTS public.target_component;
DROP SEQUENCE IF EXISTS public.target_class_id_seq;
DROP TABLE IF EXISTS public.target_class;
DROP SEQUENCE IF EXISTS public.synonyms_syn_id_seq;
DROP TABLE IF EXISTS public.synonyms;
DROP SEQUENCE IF EXISTS public.structures_cd_id_seq;
DROP TABLE IF EXISTS public.structures;
DROP SEQUENCE IF EXISTS public.structure_type_id_seq;
DROP TABLE IF EXISTS public.structure_type;
DROP SEQUENCE IF EXISTS public.struct_type_def_id_seq;
DROP TABLE IF EXISTS public.struct_type_def;
DROP TABLE IF EXISTS public.struct2parent;
DROP SEQUENCE IF EXISTS public.struct2drgclass_id_seq;
DROP TABLE IF EXISTS public.struct2drgclass;
DROP SEQUENCE IF EXISTS public.struct2atc_id_seq;
DROP TABLE IF EXISTS public.struct2atc;
DROP SEQUENCE IF EXISTS public.section_id_seq;
DROP TABLE IF EXISTS public.section;
DROP SEQUENCE IF EXISTS public.reference_id_seq;
DROP TABLE IF EXISTS public.reference;
DROP SEQUENCE IF EXISTS public.ref_type_id_seq;
DROP TABLE IF EXISTS public.ref_type;
DROP SEQUENCE IF EXISTS public.protein_type_id_seq;
DROP TABLE IF EXISTS public.protein_type;
DROP SEQUENCE IF EXISTS public.property_type_id_seq;
DROP TABLE IF EXISTS public.property_type;
DROP SEQUENCE IF EXISTS public.property_id_seq;
DROP TABLE IF EXISTS public.property;
DROP SEQUENCE IF EXISTS public.product_id_seq;
DROP TABLE IF EXISTS public.product;
DROP SEQUENCE IF EXISTS public.prd2label_id_seq;
DROP TABLE IF EXISTS public.prd2label;
DROP SEQUENCE IF EXISTS public.pka_id_seq;
DROP TABLE IF EXISTS public.pka;
DROP SEQUENCE IF EXISTS public.pharma_class_id_seq;
DROP TABLE IF EXISTS public.pharma_class;
DROP SEQUENCE IF EXISTS public.pdb_id_seq;
DROP TABLE IF EXISTS public.pdb;
DROP SEQUENCE IF EXISTS public.parentmol_cd_id_seq;
DROP TABLE IF EXISTS public.parentmol;
DROP SEQUENCE IF EXISTS public.omop_relationship_id_seq;
DROP VIEW IF EXISTS public.omop_relationship_doid_view;
DROP TABLE IF EXISTS public.omop_relationship;
DROP SEQUENCE IF EXISTS public.ob_product_id_seq;
DROP VIEW IF EXISTS public.ob_patent_view;
DROP TABLE IF EXISTS public.ob_patent_use_code;
DROP SEQUENCE IF EXISTS public.ob_patent_id_seq;
DROP TABLE IF EXISTS public.ob_patent;
DROP VIEW IF EXISTS public.ob_exclusivity_view;
DROP TABLE IF EXISTS public.struct2obprod;
DROP TABLE IF EXISTS public.ob_product;
DROP SEQUENCE IF EXISTS public.ob_exclusivity_id_seq;
DROP TABLE IF EXISTS public.ob_exclusivity_code;
DROP TABLE IF EXISTS public.ob_exclusivity;
DROP SEQUENCE IF EXISTS public.lincs_signature_id_seq;
DROP TABLE IF EXISTS public.lincs_signature;
DROP TABLE IF EXISTS public.label;
DROP SEQUENCE IF EXISTS public.inn_stem_id_seq;
DROP TABLE IF EXISTS public.inn_stem;
DROP TABLE IF EXISTS public.ijc_connect_structures;
DROP TABLE IF EXISTS public.ijc_connect_items;
DROP SEQUENCE IF EXISTS public.identifier_id_seq;
DROP TABLE IF EXISTS public.identifier;
DROP SEQUENCE IF EXISTS public.id_type_id_seq;
DROP TABLE IF EXISTS public.id_type;
DROP TABLE IF EXISTS public.humanim;
DROP VIEW IF EXISTS public.faers_top;
DROP SEQUENCE IF EXISTS public.faers_ped_id_seq;
DROP TABLE IF EXISTS public.faers_ped;
DROP SEQUENCE IF EXISTS public.faers_male_id_seq;
DROP TABLE IF EXISTS public.faers_male;
DROP SEQUENCE IF EXISTS public.faers_id_seq;
DROP SEQUENCE IF EXISTS public.faers_ger_id_seq;
DROP TABLE IF EXISTS public.faers_ger;
DROP SEQUENCE IF EXISTS public.faers_female_id_seq;
DROP TABLE IF EXISTS public.faers_female;
DROP TABLE IF EXISTS public.faers;
DROP SEQUENCE IF EXISTS public.drug_class_id_seq;
DROP TABLE IF EXISTS public.drug_class;
DROP SEQUENCE IF EXISTS public.doid_xref_id_seq;
DROP TABLE IF EXISTS public.doid_xref;
DROP TABLE IF EXISTS public.doid;
DROP SEQUENCE IF EXISTS public.ddi_risk_id_seq;
DROP TABLE IF EXISTS public.ddi_risk;
DROP SEQUENCE IF EXISTS public.ddi_id_seq;
DROP TABLE IF EXISTS public.ddi;
DROP TABLE IF EXISTS public.dbversion;
DROP TABLE IF EXISTS public.data_source;
DROP SEQUENCE IF EXISTS public.attr_type_id_seq;
DROP TABLE IF EXISTS public.attr_type;
DROP SEQUENCE IF EXISTS public.atc_id_seq;
DROP SEQUENCE IF EXISTS public.atc_ddd_id_seq;
DROP TABLE IF EXISTS public.atc_ddd;
DROP TABLE IF EXISTS public.atc;
DROP SEQUENCE IF EXISTS public.approval_type_id_seq;
DROP TABLE IF EXISTS public.approval_type;
DROP SEQUENCE IF EXISTS public.approval_id_seq;
DROP TABLE IF EXISTS public.approval;
DROP SEQUENCE IF EXISTS public.active_ingredient_id_seq;
DROP TABLE IF EXISTS public.active_ingredient;
DROP SEQUENCE IF EXISTS public.action_type_id_seq;
DROP TABLE IF EXISTS public.action_type;
DROP TABLE IF EXISTS public.act_table_full;
DROP SCHEMA IF EXISTS public;

CREATE SCHEMA public;

COMMENT ON SCHEMA public IS 'standard public schema';

SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE public.act_table_full (
    act_id integer NOT NULL,
    struct_id integer NOT NULL,
    target_id integer NOT NULL,
    target_name character varying(200),
    target_class character varying(50),
    accession character varying(1000),
    gene character varying(1000),
    swissprot character varying(1000),
    act_value double precision,
    act_unit character varying(100),
    act_type character varying(100),
    act_comment character varying(1000),
    act_source character varying(100),
    relation character varying(5),
    moa smallint,
    moa_source character varying(100),
    act_source_url character varying(500),
    moa_source_url character varying(500),
    action_type character varying(50),
    first_in_class smallint,
    tdl character varying(500),
    act_ref_id integer,
    moa_ref_id integer,
    organism character varying(150)
);

COMMENT ON TABLE public.act_table_full IS 'bioactivity data aggregated from multiple resources';

CREATE TABLE public.action_type (
    id integer NOT NULL,
    action_type character varying(50) NOT NULL,
    description character varying(200) NOT NULL,
    parent_type character varying(50)
);

COMMENT ON TABLE public.action_type IS 'drug modulatory action types';

CREATE SEQUENCE public.action_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.action_type_id_seq OWNED BY public.action_type.id;

CREATE TABLE public.active_ingredient (
    id integer NOT NULL,
    active_moiety_unii character varying(20),
    active_moiety_name character varying(4000),
    unit character varying(20),
    quantity double precision,
    substance_unii character varying(20),
    substance_name character varying(4000),
    ndc_product_code character varying(20),
    struct_id integer,
    quantity_denom_unit character varying(20),
    quantity_denom_value double precision
);

COMMENT ON TABLE public.active_ingredient IS 'active ingredients listed in FDA drug labels';

CREATE SEQUENCE public.active_ingredient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.active_ingredient_id_seq OWNED BY public.active_ingredient.id;

CREATE TABLE public.approval (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    approval date,
    type character varying(200) NOT NULL,
    applicant character varying(100),
    orphan boolean
);

COMMENT ON TABLE public.approval IS 'approval dates by drug regualtory agencies';

CREATE SEQUENCE public.approval_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.approval_id_seq OWNED BY public.approval.id;

CREATE TABLE public.approval_type (
    id integer NOT NULL,
    descr character varying(200)
);

COMMENT ON TABLE public.approval_type IS 'listing of drug regulatory agencies';

CREATE SEQUENCE public.approval_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.approval_type_id_seq OWNED BY public.approval_type.id;

CREATE TABLE public.atc (
    id integer NOT NULL,
    code character(7) NOT NULL,
    chemical_substance character varying(250) NOT NULL,
    l1_code character(1) NOT NULL,
    l1_name character varying(200) NOT NULL,
    l2_code character(3) NOT NULL,
    l2_name character varying(200) NOT NULL,
    l3_code character(4) NOT NULL,
    l3_name character varying(200) NOT NULL,
    l4_code character(5) NOT NULL,
    l4_name character varying(200) NOT NULL,
    chemical_substance_count integer
);

COMMENT ON TABLE public.atc IS 'WHO ATC codes';

CREATE TABLE public.atc_ddd (
    id integer NOT NULL,
    atc_code character(7),
    ddd real NOT NULL,
    unit_type character varying(10),
    route character varying(20),
    comment character varying(100),
    struct_id integer NOT NULL
);

COMMENT ON TABLE public.atc_ddd IS 'WHO Defined Daily Dose, the DDD is the assumed average maintenance dose per day for a drug used for its main indication in adults';

CREATE SEQUENCE public.atc_ddd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.atc_ddd_id_seq OWNED BY public.atc_ddd.id;

CREATE SEQUENCE public.atc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.atc_id_seq OWNED BY public.atc.id;

CREATE TABLE public.attr_type (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(20) NOT NULL
);

COMMENT ON TABLE public.attr_type IS 'listing of generic attribute types';

CREATE SEQUENCE public.attr_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.attr_type_id_seq OWNED BY public.attr_type.id;

CREATE TABLE public.data_source (
    src_id smallint NOT NULL,
    source_name character varying(100)
);

COMMENT ON TABLE public.data_source IS 'listing of datasources';

CREATE TABLE public.dbversion (
    version bigint NOT NULL,
    dtime timestamp without time zone NOT NULL
);

COMMENT ON TABLE public.dbversion IS 'current database version';

CREATE TABLE public.ddi (
    id integer NOT NULL,
    drug_class1 character varying(500) NOT NULL,
    drug_class2 character varying(500) NOT NULL,
    ddi_ref_id integer NOT NULL,
    ddi_risk character varying(200) NOT NULL,
    description character varying(4000),
    source_id character varying(200)
);

COMMENT ON TABLE public.ddi IS 'Drug-Drug and Drug class - Drug class interaction table';

CREATE SEQUENCE public.ddi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ddi_id_seq OWNED BY public.ddi.id;

CREATE TABLE public.ddi_risk (
    id integer NOT NULL,
    risk character varying(200) NOT NULL,
    ddi_ref_id integer NOT NULL
);

COMMENT ON TABLE public.ddi_risk IS 'Qualitative assesments of drug-drug interactions severity';

CREATE SEQUENCE public.ddi_risk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ddi_risk_id_seq OWNED BY public.ddi_risk.id;

CREATE TABLE public.doid (
    id integer NOT NULL,
    label character varying(1000),
    doid character varying(50),
    url character varying(100)
);

COMMENT ON TABLE public.doid IS 'listing on Disease-Ontology concepts';

CREATE TABLE public.doid_xref (
    id integer NOT NULL,
    doid character varying(50),
    source character varying(50),
    xref character varying(50)
);

COMMENT ON TABLE public.doid_xref IS 'Disease-Ontology terms mappings to external resources';

CREATE SEQUENCE public.doid_xref_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.doid_xref_id_seq OWNED BY public.doid_xref.id;

CREATE TABLE public.drug_class (
    id integer NOT NULL,
    name character varying(500) NOT NULL,
    is_group smallint DEFAULT 0 NOT NULL,
    source character varying(100)
);

COMMENT ON TABLE public.drug_class IS 'groupings of drugs used to derive Drug-Drug and Drug class - Drug class interactions';

CREATE SEQUENCE public.drug_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.drug_class_id_seq OWNED BY public.drug_class.id;

CREATE TABLE public.faers (
    id integer NOT NULL,
    struct_id integer,
    meddra_name character varying(200) NOT NULL,
    meddra_code bigint NOT NULL,
    level character varying(5),
    llr double precision,
    llr_threshold double precision,
    drug_ae integer,
    drug_no_ae integer,
    no_drug_ae integer,
    no_drug_no_ae integer
);

COMMENT ON TABLE public.faers IS 'Adverse events from FDA FAERS database';
COMMENT ON COLUMN public.faers.llr IS 'Likelihood Ratio based on method described in http://dx.doi.org/10.1198/jasa.2011.ap10243';
COMMENT ON COLUMN public.faers.llr_threshold IS 'Likelihood Ratio threshold based on method described in http://dx.doi.org/10.1198/jasa.2011.ap10243';
COMMENT ON COLUMN public.faers.drug_ae IS 'number of patients taking drug and having adverse event';
COMMENT ON COLUMN public.faers.drug_no_ae IS 'number of patients taking drug and not having adverse event';
COMMENT ON COLUMN public.faers.no_drug_ae IS 'number of patients not taking drug and having adverse event';
COMMENT ON COLUMN public.faers.no_drug_no_ae IS 'number of patients not taking drug and not having adverse event';

CREATE TABLE public.faers_female (
    id integer NOT NULL,
    struct_id integer,
    meddra_name character varying(200) NOT NULL,
    meddra_code bigint NOT NULL,
    level character varying(5),
    llr double precision,
    llr_threshold double precision,
    drug_ae integer,
    drug_no_ae integer,
    no_drug_ae integer,
    no_drug_no_ae integer
);

CREATE SEQUENCE public.faers_female_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.faers_female_id_seq OWNED BY public.faers_female.id;

CREATE TABLE public.faers_ger (
    id integer NOT NULL,
    struct_id integer,
    meddra_name character varying(200) NOT NULL,
    meddra_code bigint NOT NULL,
    level character varying(5),
    llr double precision,
    llr_threshold double precision,
    drug_ae integer,
    drug_no_ae integer,
    no_drug_ae integer,
    no_drug_no_ae integer
);

CREATE SEQUENCE public.faers_ger_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.faers_ger_id_seq OWNED BY public.faers_ger.id;

CREATE SEQUENCE public.faers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.faers_id_seq OWNED BY public.faers.id;

CREATE TABLE public.faers_male (
    id integer NOT NULL,
    struct_id integer,
    meddra_name character varying(200) NOT NULL,
    meddra_code bigint NOT NULL,
    level character varying(5),
    llr double precision,
    llr_threshold double precision,
    drug_ae integer,
    drug_no_ae integer,
    no_drug_ae integer,
    no_drug_no_ae integer
);

CREATE SEQUENCE public.faers_male_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.faers_male_id_seq OWNED BY public.faers_male.id;

CREATE TABLE public.faers_ped (
    id integer NOT NULL,
    struct_id integer,
    meddra_name character varying(200) NOT NULL,
    meddra_code bigint NOT NULL,
    level character varying(5),
    llr double precision,
    llr_threshold double precision,
    drug_ae integer,
    drug_no_ae integer,
    no_drug_ae integer,
    no_drug_no_ae integer
);

CREATE SEQUENCE public.faers_ped_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.faers_ped_id_seq OWNED BY public.faers_ped.id;

CREATE VIEW public.faers_top AS
 SELECT rank_filter.struct_id,
    rank_filter.meddra_name
   FROM ( SELECT faers.id,
            faers.struct_id,
            faers.meddra_name,
            faers.meddra_code,
            faers.level,
            faers.llr,
            faers.llr_threshold,
            faers.drug_ae,
            faers.drug_no_ae,
            faers.no_drug_ae,
            faers.no_drug_no_ae,
            rank() OVER (PARTITION BY faers.struct_id ORDER BY faers.llr DESC) AS rank
           FROM public.faers
          WHERE (faers.llr > faers.llr_threshold)) rank_filter
  WHERE (rank_filter.rank <= 30);

CREATE TABLE public.humanim (
    struct_id integer,
    human boolean,
    animal boolean
);

CREATE TABLE public.id_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL,
    description character varying(500),
    url character varying(500)
);

COMMENT ON TABLE public.id_type IS 'list external identifiers sources';

CREATE SEQUENCE public.id_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.id_type_id_seq OWNED BY public.id_type.id;

CREATE TABLE public.identifier (
    id integer NOT NULL,
    identifier character varying(50) NOT NULL,
    id_type character varying(50) NOT NULL,
    struct_id integer NOT NULL,
    parent_match boolean
);

COMMENT ON TABLE public.identifier IS 'mapping to external drug resouces';

CREATE SEQUENCE public.identifier_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.identifier_id_seq OWNED BY public.identifier.id;

CREATE TABLE public.ijc_connect_items (
    id character varying(32) NOT NULL,
    username character varying(128),
    type character varying(200) NOT NULL,
    data text
);

CREATE TABLE public.ijc_connect_structures (
    id character varying(32) NOT NULL,
    structure_hash character varying(64) NOT NULL,
    structure text
);

CREATE TABLE public.inn_stem (
    id integer NOT NULL,
    stem character varying(50),
    definition character varying(1000) NOT NULL,
    national_name character varying(20),
    length smallint,
    discontinued boolean
);

COMMENT ON TABLE public.inn_stem IS 'listing of WHO INN stems based on http://www.who.int/medicines/services/inn/stembook/en/';

CREATE SEQUENCE public.inn_stem_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.inn_stem_id_seq OWNED BY public.inn_stem.id;

CREATE TABLE public.label (
    id character varying(50) NOT NULL,
    category character varying(100),
    title character varying(1000),
    effective_date date,
    assigned_entity character varying(500),
    pdf_url character varying(500)
);

COMMENT ON TABLE public.label IS 'FDA drug labels SPL identifiers and categories';

CREATE TABLE public.lincs_signature (
    id integer NOT NULL,
    struct_id1 integer,
    struct_id2 integer,
    is_parent1 boolean,
    is_parent2 boolean,
    cell_id character varying(10),
    rmsd double precision,
    rmsd_norm double precision,
    pearson double precision,
    euclid double precision
);

CREATE SEQUENCE public.lincs_signature_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.lincs_signature_id_seq OWNED BY public.lincs_signature.id;

CREATE TABLE public.ob_exclusivity (
    id integer NOT NULL,
    appl_type character(1),
    appl_no character(6),
    product_no character(3),
    exclusivity_code character varying(10),
    exclusivity_date date
);

COMMENT ON TABLE public.ob_exclusivity IS 'Exclusivity data for FDA Orange book pharmaceutical products';

CREATE TABLE public.ob_exclusivity_code (
    code character varying(10) NOT NULL,
    description character varying(500)
);

COMMENT ON TABLE public.ob_exclusivity_code IS 'Exclusivity codes from FDA Orange book';

CREATE SEQUENCE public.ob_exclusivity_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ob_exclusivity_id_seq OWNED BY public.ob_exclusivity.id;

CREATE TABLE public.ob_product (
    id integer NOT NULL,
    ingredient character varying(500),
    trade_name character varying(200),
    applicant character varying(50),
    strength character varying(500),
    appl_type character(1),
    appl_no character(6),
    te_code character varying(20),
    approval_date date,
    rld smallint,
    type character varying(5),
    applicant_full_name character varying(200),
    dose_form character varying(50),
    route character varying(100),
    product_no character(3)
);

COMMENT ON TABLE public.ob_product IS 'FDA Orange book pharmaceutical products';

CREATE TABLE public.struct2obprod (
    struct_id integer NOT NULL,
    prod_id integer NOT NULL,
    strength character varying(4000)
);

CREATE VIEW public.ob_exclusivity_view AS
 SELECT DISTINCT ON (struct2obprod.struct_id, struct2obprod.strength, ob_product.trade_name, ob_product.applicant, ob_product.appl_type, ob_product.appl_no, ob_product.type, ob_product.dose_form, ob_product.route, ob_exclusivity.exclusivity_date, ob_exclusivity_code.description) struct2obprod.struct_id,
    struct2obprod.strength,
    ob_product.trade_name,
    ob_product.applicant,
    ob_product.appl_type,
    ob_product.appl_no,
    ob_product.approval_date,
    ob_product.type,
    ob_product.dose_form,
    ob_product.route,
    ob_exclusivity.exclusivity_date,
    ob_exclusivity_code.description
   FROM public.ob_product,
    public.ob_exclusivity,
    public.ob_exclusivity_code,
    public.struct2obprod
  WHERE ((ob_product.appl_no = ob_exclusivity.appl_no) AND (ob_product.product_no = ob_exclusivity.product_no) AND ((ob_exclusivity.exclusivity_code)::text = (ob_exclusivity_code.code)::text) AND (ob_product.id = struct2obprod.prod_id));

CREATE TABLE public.ob_patent (
    id integer NOT NULL,
    appl_type character(1),
    appl_no character(6),
    product_no character(3),
    patent_no character varying(200),
    patent_expire_date date,
    drug_substance_flag character(1),
    drug_product_flag character(1),
    patent_use_code character varying(10),
    delist_flag character(1)
);

COMMENT ON TABLE public.ob_patent IS 'Patent data for FDA Orange book pharmaceutical products';

CREATE SEQUENCE public.ob_patent_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ob_patent_id_seq OWNED BY public.ob_patent.id;

CREATE TABLE public.ob_patent_use_code (
    code character varying(10) NOT NULL,
    description character varying(500)
);

COMMENT ON TABLE public.ob_patent_use_code IS 'Patent use codes from FDA Orange book';

CREATE VIEW public.ob_patent_view AS
 SELECT DISTINCT ON (struct2obprod.struct_id, struct2obprod.strength, ob_patent.patent_no, ob_patent_use_code.description, ob_product.trade_name, ob_product.applicant, ob_product.appl_type, ob_product.appl_no, ob_product.type, ob_product.dose_form, ob_product.route) struct2obprod.struct_id,
    struct2obprod.strength,
    ob_patent.patent_no,
    ob_patent_use_code.description,
    ob_product.trade_name,
    ob_product.applicant,
    ob_product.appl_type,
    ob_product.appl_no,
    ob_product.type,
    ob_product.dose_form,
    ob_product.route,
    ob_product.approval_date,
    ob_patent.patent_expire_date
   FROM public.ob_product,
    public.ob_patent,
    public.ob_patent_use_code,
    public.struct2obprod
  WHERE ((ob_product.appl_no = ob_patent.appl_no) AND (ob_product.product_no = ob_patent.product_no) AND ((ob_patent.patent_use_code)::text = (ob_patent_use_code.code)::text) AND (ob_product.id = struct2obprod.prod_id));

CREATE SEQUENCE public.ob_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ob_product_id_seq OWNED BY public.ob_product.id;

CREATE TABLE public.omop_relationship (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    concept_id integer NOT NULL,
    relationship_name character varying(256) NOT NULL,
    concept_name character varying(256) NOT NULL,
    umls_cui character(8),
    snomed_full_name character varying(500),
    cui_semantic_type character(4),
    snomed_conceptid bigint
);

COMMENT ON TABLE public.omop_relationship IS 'drug indications/contra-indications/off-label use based on OMOP v4 and manual annotations';

CREATE VIEW public.omop_relationship_doid_view AS
 SELECT omop_relationship.id,
    omop_relationship.struct_id,
    omop_relationship.concept_id,
    omop_relationship.relationship_name,
    omop_relationship.concept_name,
    omop_relationship.umls_cui,
    omop_relationship.snomed_full_name,
    omop_relationship.cui_semantic_type,
    omop_relationship.snomed_conceptid,
    d.doid
   FROM (public.omop_relationship
     LEFT JOIN ( SELECT doid_xref.xref,
            string_agg((doid_xref.doid)::text, ','::text) AS doid
           FROM public.doid_xref
          WHERE ((doid_xref.source)::text ~~ 'SNOMED%'::text)
          GROUP BY doid_xref.xref) d ON ((omop_relationship.snomed_conceptid = (d.xref)::bigint)));

CREATE SEQUENCE public.omop_relationship_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.omop_relationship_id_seq OWNED BY public.omop_relationship.id;

CREATE TABLE public.parentmol (
    cd_id integer NOT NULL,
    name character varying(250),
    cas_reg_no character varying(50),
    inchi character varying(32672),
    nostereo_inchi character varying(32672),
    molfile text,
    molimg bytea,
    smiles character varying(32672),
    inchikey character(27)
);

COMMENT ON TABLE public.parentmol IS 'parent drug molecules for active ingredients formulated as prodrugs';

CREATE SEQUENCE public.parentmol_cd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.parentmol_cd_id_seq OWNED BY public.parentmol.cd_id;

CREATE TABLE public.pdb (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    pdb character(4) NOT NULL,
    chain_id character varying(3),
    accession character varying(20),
    title character varying(1000),
    pubmed_id integer,
    exp_method character varying(50),
    deposition_date date,
    ligand_id character varying(20)
);

COMMENT ON TABLE public.pdb IS 'mapping to PDB protein-drug complexes';

CREATE SEQUENCE public.pdb_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.pdb_id_seq OWNED BY public.pdb.id;

CREATE TABLE public.pharma_class (
    id integer NOT NULL,
    struct_id integer,
    type character varying(20) NOT NULL,
    name character varying(1000) NOT NULL,
    class_code character varying(20),
    source character varying(100)
);

COMMENT ON TABLE public.pharma_class IS 'pharmacologic classifications of drugs from multiple resources';

CREATE SEQUENCE public.pharma_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.pharma_class_id_seq OWNED BY public.pharma_class.id;

CREATE TABLE public.pka (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    pka_level character varying(5),
    value double precision,
    pka_type character(1) NOT NULL
);

COMMENT ON TABLE public.pka IS 'logarithm of acid dissociation constant calculated using MoKa 3.0.0';

CREATE SEQUENCE public.pka_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.pka_id_seq OWNED BY public.pka.id;

CREATE TABLE public.prd2label (
    ndc_product_code character varying(20) NOT NULL,
    label_id character varying(50) NOT NULL,
    id integer NOT NULL
);

COMMENT ON TABLE public.prd2label IS 'mappings between FDA drug labels and pharmaceutical products associated with these labels';

CREATE SEQUENCE public.prd2label_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.prd2label_id_seq OWNED BY public.prd2label.id;

CREATE TABLE public.product (
    id integer NOT NULL,
    ndc_product_code character varying(20) NOT NULL,
    form character varying(250),
    generic_name character varying(4000),
    product_name character varying(1000),
    route character varying(50),
    marketing_status character varying(500),
    active_ingredient_count integer
);

COMMENT ON TABLE public.product IS 'pharmaceutical products associated with FDA drug labels';

CREATE SEQUENCE public.product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;

CREATE TABLE public.property (
    id integer NOT NULL,
    property_type_id integer,
    property_type_symbol character varying(10),
    struct_id integer,
    value double precision,
    reference_id integer,
    reference_type character varying(50),
    source character varying(80)
);

CREATE SEQUENCE public.property_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.property_id_seq OWNED BY public.property.id;

CREATE TABLE public.property_type (
    id integer NOT NULL,
    category character varying(20),
    name character varying(80),
    symbol character varying(10),
    units character varying(10)
);

CREATE SEQUENCE public.property_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.property_type_id_seq OWNED BY public.property_type.id;

CREATE TABLE public.protein_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL
);

COMMENT ON TABLE public.protein_type IS 'simple classification of protein types interacting with drugs';

CREATE SEQUENCE public.protein_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.protein_type_id_seq OWNED BY public.protein_type.id;

CREATE TABLE public.ref_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL
);

COMMENT ON TABLE public.ref_type IS 'listing of reference types';

CREATE SEQUENCE public.ref_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.ref_type_id_seq OWNED BY public.ref_type.id;

CREATE TABLE public.reference (
    id integer NOT NULL,
    pmid integer,
    doi character varying(50),
    document_id character varying(200),
    type character varying(50),
    authors character varying(4000),
    title character varying(500),
    isbn10 character(10),
    url character varying(1000),
    journal character varying(100),
    volume character varying(20),
    issue character varying(20),
    dp_year integer,
    pages character varying(50)
);

COMMENT ON TABLE public.reference IS 'external references for drug bioactivities and mechanism of action';

CREATE SEQUENCE public.reference_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.reference_id_seq OWNED BY public.reference.id;

CREATE TABLE public.section (
    id integer NOT NULL,
    text text,
    label_id character varying(50),
    code character varying(20),
    title character varying(4000)
);

COMMENT ON TABLE public.section IS 'FDA SPL drug label sections';

CREATE SEQUENCE public.section_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.section_id_seq OWNED BY public.section.id;

CREATE TABLE public.struct2atc (
    struct_id integer NOT NULL,
    atc_code character(7) NOT NULL,
    id integer NOT NULL
);

COMMENT ON TABLE public.struct2atc IS 'mapping between structures table and WHO ATC codes';

CREATE SEQUENCE public.struct2atc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.struct2atc_id_seq OWNED BY public.struct2atc.id;

CREATE TABLE public.struct2drgclass (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    drug_class_id integer NOT NULL
);

COMMENT ON TABLE public.struct2drgclass IS 'mapping between structures and drug_class tables';

CREATE SEQUENCE public.struct2drgclass_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.struct2drgclass_id_seq OWNED BY public.struct2drgclass.id;

CREATE TABLE public.struct2parent (
    struct_id integer NOT NULL,
    parent_id integer NOT NULL
);

COMMENT ON TABLE public.struct2parent IS 'mapping between prodrugs in structures table and active parent molecules';

CREATE TABLE public.struct_type_def (
    id integer NOT NULL,
    type character varying(50),
    description character varying(200)
);

COMMENT ON TABLE public.struct_type_def IS 'simple classification of chemical entities in structures table';

CREATE SEQUENCE public.struct_type_def_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.struct_type_def_id_seq OWNED BY public.struct_type_def.id;

CREATE TABLE public.structure_type (
    id integer NOT NULL,
    struct_id integer,
    type character varying(50) DEFAULT 'UNKNOWN'::character varying NOT NULL
);

COMMENT ON TABLE public.structure_type IS 'mapping between chemical entities in structures table and types defined in struct_type_def';

CREATE SEQUENCE public.structure_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.structure_type_id_seq OWNED BY public.structure_type.id;

CREATE TABLE public.structures (
    cd_id integer NOT NULL,
    cd_formula character varying(100),
    cd_molweight double precision,
    id integer NOT NULL,
    clogp double precision,
    alogs double precision,
    cas_reg_no character varying(50),
    tpsa real,
    lipinski integer,
    name character varying(250),
    no_formulations integer,
    stem character varying(50),
    molfile text,
    mrdef character varying(32672),
    enhanced_stereo boolean DEFAULT false NOT NULL,
    arom_c integer,
    sp3_c integer,
    sp2_c integer,
    sp_c integer,
    halogen integer,
    hetero_sp2_c integer,
    rotb integer,
    molimg bytea,
    o_n integer,
    oh_nh integer,
    inchi character varying(32672),
    smiles character varying(32672),
    rgb integer,
    fda_labels integer,
    inchikey character(27),
    status character varying(10)
);

COMMENT ON TABLE public.structures IS 'chemical entities in active pharmaceutical ingredients table';
COMMENT ON COLUMN public.structures.rgb IS 'number of rigid bonds';

CREATE SEQUENCE public.structures_cd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.structures_cd_id_seq OWNED BY public.structures.cd_id;

CREATE TABLE public.synonyms (
    syn_id integer NOT NULL,
    id integer,
    name character varying(250) NOT NULL,
    preferred_name smallint,
    parent_id integer,
    lname character varying(250) DEFAULT 'GENERATED ALWAYS AS ( LCASE(NAME) )'::character varying
);

COMMENT ON TABLE public.synonyms IS 'unamiguous list of synonyms assigned to chemical entities in structures and parentmol tables';

CREATE SEQUENCE public.synonyms_syn_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.synonyms_syn_id_seq OWNED BY public.synonyms.syn_id;

CREATE TABLE public.target_class (
    l1 character varying(50) NOT NULL,
    id integer NOT NULL
);

COMMENT ON TABLE public.target_class IS 'ChEMBL-db target classification system, level 1 only';

CREATE SEQUENCE public.target_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.target_class_id_seq OWNED BY public.target_class.id;

CREATE TABLE public.target_component (
    id integer NOT NULL,
    accession character varying(20),
    swissprot character varying(20),
    organism character varying(150),
    name character varying(200),
    gene character varying(25),
    geneid bigint,
    tdl character varying(5)
);

COMMENT ON TABLE public.target_component IS 'protein components of taregt interacting with drugs';

CREATE SEQUENCE public.target_component_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.target_component_id_seq OWNED BY public.target_component.id;

CREATE TABLE public.target_dictionary (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    target_class character varying(50) DEFAULT 'Unclassified'::character varying NOT NULL,
    protein_components smallint DEFAULT 0 NOT NULL,
    protein_type character varying(50),
    tdl character varying(500)
);

COMMENT ON TABLE public.target_dictionary IS 'target entities interacting with drugs';

CREATE SEQUENCE public.target_dictionary_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.target_dictionary_id_seq OWNED BY public.target_dictionary.id;

CREATE TABLE public.target_go (
    id character(10) NOT NULL,
    term character varying(200),
    type character(1)
);

COMMENT ON TABLE public.target_go IS 'Gene Ontology terms';

CREATE TABLE public.target_keyword (
    id character(7) NOT NULL,
    descr character varying(4000),
    category character varying(50),
    keyword character varying(200)
);

COMMENT ON TABLE public.target_keyword IS 'keywords extracted from Unirpot protein entries';

CREATE TABLE public.td2tc (
    target_id integer NOT NULL,
    component_id integer NOT NULL
);

COMMENT ON TABLE public.td2tc IS 'mapping between drug target entities and protein components';

CREATE TABLE public.tdgo2tc (
    id integer NOT NULL,
    go_id character(10) NOT NULL,
    component_id integer
);

COMMENT ON TABLE public.tdgo2tc IS 'mapping between protein components and GO terms';

CREATE SEQUENCE public.tdgo2tc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.tdgo2tc_id_seq OWNED BY public.tdgo2tc.id;

CREATE TABLE public.tdkey2tc (
    id integer NOT NULL,
    tdkey_id character(7) NOT NULL,
    component_id integer
);

COMMENT ON TABLE public.tdkey2tc IS 'mapping between protein components and Uniprot keywords';

CREATE SEQUENCE public.tdkey2tc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.tdkey2tc_id_seq OWNED BY public.tdkey2tc.id;

CREATE TABLE public.vetomop (
    omopid integer NOT NULL,
    struct_id integer,
    species character varying(100),
    relationship_type character varying(50),
    concept_name character varying(500)
);

CREATE SEQUENCE public.vetomop_omopid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.vetomop_omopid_seq OWNED BY public.vetomop.omopid;

CREATE TABLE public.vetprod (
    prodid integer NOT NULL,
    appl_type character(1) NOT NULL,
    appl_no character(7) NOT NULL,
    trade_name character varying(200),
    applicant character varying(100),
    active_ingredients_count integer
);

CREATE TABLE public.vetprod2struct (
    prodid integer,
    struct_id integer
);

CREATE SEQUENCE public.vetprod_prodid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.vetprod_prodid_seq OWNED BY public.vetprod.prodid;

CREATE TABLE public.vetprod_type (
    id integer NOT NULL,
    appl_type character(1) NOT NULL,
    description character varying(11)
);

CREATE TABLE public.vettype (
    prodid integer,
    type character varying(3)
);

ALTER TABLE ONLY public.action_type ALTER COLUMN id SET DEFAULT nextval('public.action_type_id_seq'::regclass);
ALTER TABLE ONLY public.active_ingredient ALTER COLUMN id SET DEFAULT nextval('public.active_ingredient_id_seq'::regclass);
ALTER TABLE ONLY public.approval ALTER COLUMN id SET DEFAULT nextval('public.approval_id_seq'::regclass);
ALTER TABLE ONLY public.approval_type ALTER COLUMN id SET DEFAULT nextval('public.approval_type_id_seq'::regclass);
ALTER TABLE ONLY public.atc ALTER COLUMN id SET DEFAULT nextval('public.atc_id_seq'::regclass);
ALTER TABLE ONLY public.atc_ddd ALTER COLUMN id SET DEFAULT nextval('public.atc_ddd_id_seq'::regclass);
ALTER TABLE ONLY public.attr_type ALTER COLUMN id SET DEFAULT nextval('public.attr_type_id_seq'::regclass);
ALTER TABLE ONLY public.ddi ALTER COLUMN id SET DEFAULT nextval('public.ddi_id_seq'::regclass);
ALTER TABLE ONLY public.ddi_risk ALTER COLUMN id SET DEFAULT nextval('public.ddi_risk_id_seq'::regclass);
ALTER TABLE ONLY public.doid_xref ALTER COLUMN id SET DEFAULT nextval('public.doid_xref_id_seq'::regclass);
ALTER TABLE ONLY public.drug_class ALTER COLUMN id SET DEFAULT nextval('public.drug_class_id_seq'::regclass);
ALTER TABLE ONLY public.faers ALTER COLUMN id SET DEFAULT nextval('public.faers_id_seq'::regclass);
ALTER TABLE ONLY public.faers_female ALTER COLUMN id SET DEFAULT nextval('public.faers_female_id_seq'::regclass);
ALTER TABLE ONLY public.faers_ger ALTER COLUMN id SET DEFAULT nextval('public.faers_ger_id_seq'::regclass);
ALTER TABLE ONLY public.faers_male ALTER COLUMN id SET DEFAULT nextval('public.faers_male_id_seq'::regclass);
ALTER TABLE ONLY public.faers_ped ALTER COLUMN id SET DEFAULT nextval('public.faers_ped_id_seq'::regclass);
ALTER TABLE ONLY public.id_type ALTER COLUMN id SET DEFAULT nextval('public.id_type_id_seq'::regclass);
ALTER TABLE ONLY public.identifier ALTER COLUMN id SET DEFAULT nextval('public.identifier_id_seq'::regclass);
ALTER TABLE ONLY public.inn_stem ALTER COLUMN id SET DEFAULT nextval('public.inn_stem_id_seq'::regclass);
ALTER TABLE ONLY public.lincs_signature ALTER COLUMN id SET DEFAULT nextval('public.lincs_signature_id_seq'::regclass);
ALTER TABLE ONLY public.ob_exclusivity ALTER COLUMN id SET DEFAULT nextval('public.ob_exclusivity_id_seq'::regclass);
ALTER TABLE ONLY public.ob_patent ALTER COLUMN id SET DEFAULT nextval('public.ob_patent_id_seq'::regclass);
ALTER TABLE ONLY public.ob_product ALTER COLUMN id SET DEFAULT nextval('public.ob_product_id_seq'::regclass);
ALTER TABLE ONLY public.omop_relationship ALTER COLUMN id SET DEFAULT nextval('public.omop_relationship_id_seq'::regclass);
ALTER TABLE ONLY public.parentmol ALTER COLUMN cd_id SET DEFAULT nextval('public.parentmol_cd_id_seq'::regclass);
ALTER TABLE ONLY public.pdb ALTER COLUMN id SET DEFAULT nextval('public.pdb_id_seq'::regclass);
ALTER TABLE ONLY public.pharma_class ALTER COLUMN id SET DEFAULT nextval('public.pharma_class_id_seq'::regclass);
ALTER TABLE ONLY public.pka ALTER COLUMN id SET DEFAULT nextval('public.pka_id_seq'::regclass);
ALTER TABLE ONLY public.prd2label ALTER COLUMN id SET DEFAULT nextval('public.prd2label_id_seq'::regclass);
ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);
ALTER TABLE ONLY public.property ALTER COLUMN id SET DEFAULT nextval('public.property_id_seq'::regclass);
ALTER TABLE ONLY public.property_type ALTER COLUMN id SET DEFAULT nextval('public.property_type_id_seq'::regclass);
ALTER TABLE ONLY public.protein_type ALTER COLUMN id SET DEFAULT nextval('public.protein_type_id_seq'::regclass);
ALTER TABLE ONLY public.ref_type ALTER COLUMN id SET DEFAULT nextval('public.ref_type_id_seq'::regclass);
ALTER TABLE ONLY public.reference ALTER COLUMN id SET DEFAULT nextval('public.reference_id_seq'::regclass);
ALTER TABLE ONLY public.section ALTER COLUMN id SET DEFAULT nextval('public.section_id_seq'::regclass);
ALTER TABLE ONLY public.struct2atc ALTER COLUMN id SET DEFAULT nextval('public.struct2atc_id_seq'::regclass);
ALTER TABLE ONLY public.struct2drgclass ALTER COLUMN id SET DEFAULT nextval('public.struct2drgclass_id_seq'::regclass);
ALTER TABLE ONLY public.struct_type_def ALTER COLUMN id SET DEFAULT nextval('public.struct_type_def_id_seq'::regclass);
ALTER TABLE ONLY public.structure_type ALTER COLUMN id SET DEFAULT nextval('public.structure_type_id_seq'::regclass);
ALTER TABLE ONLY public.structures ALTER COLUMN cd_id SET DEFAULT nextval('public.structures_cd_id_seq'::regclass);
ALTER TABLE ONLY public.synonyms ALTER COLUMN syn_id SET DEFAULT nextval('public.synonyms_syn_id_seq'::regclass);
ALTER TABLE ONLY public.target_class ALTER COLUMN id SET DEFAULT nextval('public.target_class_id_seq'::regclass);
ALTER TABLE ONLY public.target_component ALTER COLUMN id SET DEFAULT nextval('public.target_component_id_seq'::regclass);
ALTER TABLE ONLY public.target_dictionary ALTER COLUMN id SET DEFAULT nextval('public.target_dictionary_id_seq'::regclass);
ALTER TABLE ONLY public.tdgo2tc ALTER COLUMN id SET DEFAULT nextval('public.tdgo2tc_id_seq'::regclass);
ALTER TABLE ONLY public.tdkey2tc ALTER COLUMN id SET DEFAULT nextval('public.tdkey2tc_id_seq'::regclass);
ALTER TABLE ONLY public.vetomop ALTER COLUMN omopid SET DEFAULT nextval('public.vetomop_omopid_seq'::regclass);
ALTER TABLE ONLY public.vetprod ALTER COLUMN prodid SET DEFAULT nextval('public.vetprod_prodid_seq'::regclass);

SELECT pg_catalog.setval('public.action_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.active_ingredient_id_seq', 1, false);
SELECT pg_catalog.setval('public.approval_id_seq', 1, false);
SELECT pg_catalog.setval('public.approval_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.atc_ddd_id_seq', 1, false);
SELECT pg_catalog.setval('public.atc_id_seq', 1, false);
SELECT pg_catalog.setval('public.attr_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.ddi_id_seq', 1, false);
SELECT pg_catalog.setval('public.ddi_risk_id_seq', 1, false);
SELECT pg_catalog.setval('public.doid_xref_id_seq', 1, false);
SELECT pg_catalog.setval('public.drug_class_id_seq', 1, false);
SELECT pg_catalog.setval('public.faers_female_id_seq', 1, false);
SELECT pg_catalog.setval('public.faers_ger_id_seq', 1, false);
SELECT pg_catalog.setval('public.faers_id_seq', 1, false);
SELECT pg_catalog.setval('public.faers_male_id_seq', 1, false);
SELECT pg_catalog.setval('public.faers_ped_id_seq', 1, false);
SELECT pg_catalog.setval('public.id_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.identifier_id_seq', 1, false);
SELECT pg_catalog.setval('public.inn_stem_id_seq', 1, false);
SELECT pg_catalog.setval('public.lincs_signature_id_seq', 1, false);
SELECT pg_catalog.setval('public.ob_exclusivity_id_seq', 1, false);
SELECT pg_catalog.setval('public.ob_patent_id_seq', 1, false);
SELECT pg_catalog.setval('public.ob_product_id_seq', 1, false);
SELECT pg_catalog.setval('public.omop_relationship_id_seq', 1, false);
SELECT pg_catalog.setval('public.parentmol_cd_id_seq', 1, false);
SELECT pg_catalog.setval('public.pdb_id_seq', 1, false);
SELECT pg_catalog.setval('public.pharma_class_id_seq', 1, false);
SELECT pg_catalog.setval('public.pka_id_seq', 1, false);
SELECT pg_catalog.setval('public.prd2label_id_seq', 137693, true);
SELECT pg_catalog.setval('public.product_id_seq', 1, false);
SELECT pg_catalog.setval('public.property_id_seq', 1, false);
SELECT pg_catalog.setval('public.property_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.protein_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.ref_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.reference_id_seq', 1, false);
SELECT pg_catalog.setval('public.section_id_seq', 1, false);
SELECT pg_catalog.setval('public.struct2atc_id_seq', 5030, true);
SELECT pg_catalog.setval('public.struct2drgclass_id_seq', 1, false);
SELECT pg_catalog.setval('public.struct_type_def_id_seq', 1, false);
SELECT pg_catalog.setval('public.structure_type_id_seq', 1, false);
SELECT pg_catalog.setval('public.structures_cd_id_seq', 1, false);
SELECT pg_catalog.setval('public.synonyms_syn_id_seq', 1, false);
SELECT pg_catalog.setval('public.target_class_id_seq', 39, true);
SELECT pg_catalog.setval('public.target_component_id_seq', 1, false);
SELECT pg_catalog.setval('public.target_dictionary_id_seq', 1, false);
SELECT pg_catalog.setval('public.tdgo2tc_id_seq', 1, false);
SELECT pg_catalog.setval('public.tdkey2tc_id_seq', 1, false);
SELECT pg_catalog.setval('public.vetomop_omopid_seq', 1, false);
SELECT pg_catalog.setval('public.vetprod_prodid_seq', 1, false);

ALTER TABLE ONLY public.vetprod_type
    ADD CONSTRAINT "SQL0000000007-f0b3c086-0182-8659-7929-00007c5ff800" PRIMARY KEY (appl_type);

ALTER TABLE ONLY public.vetprod
    ADD CONSTRAINT "SQL0000000012-af8ec0b3-0182-8659-7929-00007c5ff800" PRIMARY KEY (prodid);

ALTER TABLE ONLY public.vetomop
    ADD CONSTRAINT "SQL0000000018-6dd780d4-0182-8659-7929-00007c5ff800" PRIMARY KEY (omopid);

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT cas_reg_no_uq UNIQUE (cas_reg_no);

ALTER TABLE ONLY public.ddi_risk
    ADD CONSTRAINT ddi_risk_uq UNIQUE (risk, ddi_ref_id);

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_tuple_uq UNIQUE (drug_class1, drug_class2, ddi_ref_id);

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_unique UNIQUE (identifier, id_type, struct_id);

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT omoprel_struct_concept_uq UNIQUE (struct_id, concept_id);

ALTER TABLE ONLY public.ijc_connect_items
    ADD CONSTRAINT pk_ijc_connect_items PRIMARY KEY (id);

ALTER TABLE ONLY public.ijc_connect_structures
    ADD CONSTRAINT pk_ijc_connect_structures PRIMARY KEY (id, structure_hash);

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd2label_id_key UNIQUE (id);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT prd_ndc_uniq UNIQUE (ndc_product_code);

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT sql100501171817150 PRIMARY KEY (cd_id);

ALTER TABLE ONLY public.data_source
    ADD CONSTRAINT sql100517171435170 PRIMARY KEY (src_id);

ALTER TABLE ONLY public.label
    ADD CONSTRAINT sql120404123647220 PRIMARY KEY (id);

ALTER TABLE ONLY public.section
    ADD CONSTRAINT sql120404123658530 PRIMARY KEY (id);

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT sql120404123859150 PRIMARY KEY (id);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT sql120412001426700 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT sql120523120921130 PRIMARY KEY (struct_id, atc_code);

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT sql120925163230900 PRIMARY KEY (id);

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT sql121023161238820 PRIMARY KEY (id);

ALTER TABLE ONLY public.atc
    ADD CONSTRAINT sql130424125636180 PRIMARY KEY (id);

ALTER TABLE ONLY public.atc
    ADD CONSTRAINT sql130424125636181 UNIQUE (code);

ALTER TABLE ONLY public.target_class
    ADD CONSTRAINT sql130710170452610 PRIMARY KEY (l1);

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT sql130919144750040 PRIMARY KEY (ndc_product_code, label_id);

ALTER TABLE ONLY public.inn_stem
    ADD CONSTRAINT sql140212001438200 PRIMARY KEY (id);

ALTER TABLE ONLY public.inn_stem
    ADD CONSTRAINT sql140212001438201 UNIQUE (stem);

ALTER TABLE ONLY public.ref_type
    ADD CONSTRAINT sql140401160903570 PRIMARY KEY (id);

ALTER TABLE ONLY public.ref_type
    ADD CONSTRAINT sql140401160903571 UNIQUE (type);

ALTER TABLE ONLY public.drug_class
    ADD CONSTRAINT sql140409195222370 PRIMARY KEY (id);

ALTER TABLE ONLY public.drug_class
    ADD CONSTRAINT sql140409195222371 UNIQUE (name);

ALTER TABLE ONLY public.attr_type
    ADD CONSTRAINT sql140410123913680 PRIMARY KEY (id);

ALTER TABLE ONLY public.attr_type
    ADD CONSTRAINT sql140410123913681 UNIQUE (name);

ALTER TABLE ONLY public.ddi_risk
    ADD CONSTRAINT sql140411131027290 PRIMARY KEY (id);

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT sql140411131553960 PRIMARY KEY (id);

ALTER TABLE ONLY public.atc_ddd
    ADD CONSTRAINT sql140512172435200 PRIMARY KEY (id);

ALTER TABLE ONLY public.id_type
    ADD CONSTRAINT sql140607012055120 PRIMARY KEY (id);

ALTER TABLE ONLY public.id_type
    ADD CONSTRAINT sql140607012055121 UNIQUE (type);

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT sql140607225949710 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT sql140608153701270 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT sql140608153701271 UNIQUE (struct_id, drug_class_id);

ALTER TABLE ONLY public.struct_type_def
    ADD CONSTRAINT sql141016095933600 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct_type_def
    ADD CONSTRAINT sql141016095933601 UNIQUE (type);

ALTER TABLE ONLY public.approval_type
    ADD CONSTRAINT sql141031231522060 PRIMARY KEY (id);

ALTER TABLE ONLY public.approval_type
    ADD CONSTRAINT sql141031231522061 UNIQUE (descr);

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT sql141031231617260 PRIMARY KEY (id);

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT sql141031231617263 UNIQUE (struct_id, type);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458570 PRIMARY KEY (id);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458571 UNIQUE (pmid);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458572 UNIQUE (doi);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458573 UNIQUE (document_id);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458574 UNIQUE (isbn10);

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155660 PRIMARY KEY (id);

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155670 UNIQUE (accession);

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155671 UNIQUE (swissprot);

ALTER TABLE ONLY public.protein_type
    ADD CONSTRAINT sql141203213631730 PRIMARY KEY (id);

ALTER TABLE ONLY public.protein_type
    ADD CONSTRAINT sql141203213631731 UNIQUE (type);

ALTER TABLE ONLY public.target_dictionary
    ADD CONSTRAINT sql141205191111190 PRIMARY KEY (id);

ALTER TABLE ONLY public.td2tc
    ADD CONSTRAINT sql141205191436250 PRIMARY KEY (target_id, component_id);

ALTER TABLE ONLY public.action_type
    ADD CONSTRAINT sql141210123613760 PRIMARY KEY (id);

ALTER TABLE ONLY public.action_type
    ADD CONSTRAINT sql141210123613761 UNIQUE (action_type);

ALTER TABLE ONLY public.target_keyword
    ADD CONSTRAINT sql141211160454700 PRIMARY KEY (id);

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT sql141211195643960 PRIMARY KEY (id);

ALTER TABLE ONLY public.target_go
    ADD CONSTRAINT sql141211234759820 PRIMARY KEY (id);

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT sql141211235052890 PRIMARY KEY (id);

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT sql150123095054720 PRIMARY KEY (id);

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT sql150123095054722 UNIQUE (struct_id, pdb);

ALTER TABLE ONLY public.doid
    ADD CONSTRAINT sql150425232401220 PRIMARY KEY (id);

ALTER TABLE ONLY public.doid
    ADD CONSTRAINT sql150425232401221 UNIQUE (doid);

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT sql150426005334630 PRIMARY KEY (id);

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT sql150426005334632 UNIQUE (doid, source, xref);

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184351770 PRIMARY KEY (cd_id);

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184621160 UNIQUE (name);

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184644290 UNIQUE (cas_reg_no);

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT sql150529131801300 PRIMARY KEY (struct_id, parent_id);

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT sql150603161251830 PRIMARY KEY (id);

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT sql150603161251841 UNIQUE (struct_id, type, name);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920370 PRIMARY KEY (syn_id);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920371 UNIQUE (name);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920374 UNIQUE (id, preferred_name);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920375 UNIQUE (parent_id, preferred_name);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT sql160219095125231 PRIMARY KEY (act_id);

ALTER TABLE ONLY public.dbversion
    ADD CONSTRAINT sql160415165555160 PRIMARY KEY (version);

ALTER TABLE ONLY public.ob_product
    ADD CONSTRAINT sql161126154442460 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT sql161126154450310 PRIMARY KEY (struct_id, prod_id);

ALTER TABLE ONLY public.ob_exclusivity
    ADD CONSTRAINT sql161127115514940 PRIMARY KEY (id);

ALTER TABLE ONLY public.ob_patent
    ADD CONSTRAINT sql161127120038110 PRIMARY KEY (id);

ALTER TABLE ONLY public.ob_exclusivity_code
    ADD CONSTRAINT sql161127133109120 PRIMARY KEY (code);

ALTER TABLE ONLY public.ob_patent_use_code
    ADD CONSTRAINT sql161127133626130 PRIMARY KEY (code);

ALTER TABLE ONLY public.pka
    ADD CONSTRAINT sql180408153602180 PRIMARY KEY (id);

ALTER TABLE ONLY public.faers
    ADD CONSTRAINT sql180422234202640 PRIMARY KEY (id);

ALTER TABLE ONLY public.lincs_signature
    ADD CONSTRAINT sql180509154922470 PRIMARY KEY (id);

ALTER TABLE ONLY public.lincs_signature
    ADD CONSTRAINT sql180509154922471 UNIQUE (struct_id1, struct_id2, is_parent1, is_parent2, cell_id);

ALTER TABLE ONLY public.faers_male
    ADD CONSTRAINT sql200913054154500 PRIMARY KEY (id);

ALTER TABLE ONLY public.faers_female
    ADD CONSTRAINT sql200913054200240 PRIMARY KEY (id);

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_id_key UNIQUE (id);

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT struct_id_type_uq UNIQUE (struct_id, type);

ALTER TABLE ONLY public.property_type
    ADD CONSTRAINT symbol PRIMARY KEY (id);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT syn_lname_uq UNIQUE (lname);

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT uniq_structures_id UNIQUE (id);

CREATE INDEX "SQL0000000000-6302404f-0182-82a6-63b4-00007c800000" ON public.humanim USING btree (struct_id);
CREATE INDEX "SQL0000000000-a352c053-0182-81bb-5234-00001f0067d8" ON public.faers_ped USING btree (struct_id);
CREATE INDEX "SQL0000000000-fb160050-0182-81c2-aa9c-00001f0585e8" ON public.faers_ger USING btree (struct_id);
CREATE UNIQUE INDEX "SQL0000000006-48928085-0182-8659-7929-00007c5ff800" ON public.vetprod_type USING btree (id);
CREATE INDEX "SQL0000000013-67bb80b4-0182-8659-7929-00007c5ff800" ON public.vetprod USING btree (appl_type);
CREATE UNIQUE INDEX "SQL0000000014-4fe880b5-0182-8659-7929-00007c5ff800" ON public.vetprod USING btree (appl_no);
CREATE INDEX "SQL0000000015-b278c0c3-0182-8659-7929-00007c5ff800" ON public.vetprod2struct USING btree (prodid);
CREATE INDEX "SQL0000000016-eaa980c4-0182-8659-7929-00007c5ff800" ON public.vetprod2struct USING btree (struct_id);
CREATE INDEX "SQL0000000017-d46b80cd-0182-8659-7929-00007c5ff800" ON public.vettype USING btree (prodid);
CREATE INDEX "SQL0000000019-560c80d5-0182-8659-7929-00007c5ff800" ON public.vetomop USING btree (struct_id);
CREATE UNIQUE INDEX index_ijc_connect_items ON public.ijc_connect_items USING btree (type, username);
CREATE INDEX ob_patent_applno_idx ON public.ob_patent USING btree (appl_no);
CREATE INDEX ob_patent_prodno_idx ON public.ob_patent USING btree (product_no);
CREATE INDEX ob_product_appno_idx ON public.ob_product USING btree (appl_no);
CREATE INDEX ob_product_prodno_idx ON public.ob_product USING btree (product_no);
CREATE UNIQUE INDEX sql100501183943930 ON public.structures USING btree (id);
CREATE INDEX sql120404123658531 ON public.section USING btree (label_id);
CREATE INDEX sql120404123859152 ON public.active_ingredient USING btree (struct_id);
CREATE UNIQUE INDEX sql120412004620930 ON public.product USING btree (ndc_product_code);
CREATE INDEX sql120412004634310 ON public.active_ingredient USING btree (ndc_product_code);
CREATE INDEX sql120418113711390 ON public.structures USING btree (cas_reg_no);
CREATE INDEX sql120523120921131 ON public.struct2atc USING btree (struct_id);
CREATE INDEX sql120925163230920 ON public.structure_type USING btree (struct_id);
CREATE INDEX sql120925163230921 ON public.structure_type USING btree (struct_id, type);
CREATE INDEX sql121023161238850 ON public.omop_relationship USING btree (struct_id);
CREATE INDEX xref_source_idx ON public.doid_xref USING btree (xref, source);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_act_type FOREIGN KEY (action_type) REFERENCES public.action_type(action_type);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_target_class FOREIGN KEY (target_class) REFERENCES public.target_class(l1);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_target_dict FOREIGN KEY (target_id) REFERENCES public.target_dictionary(id);

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT active_ingredient_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT active_ingredient_ndc_product_code_fkey FOREIGN KEY (ndc_product_code) REFERENCES public.product(ndc_product_code);

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT approval_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT approval_2_type FOREIGN KEY (type) REFERENCES public.approval_type(descr);

ALTER TABLE ONLY public.atc_ddd
    ADD CONSTRAINT atc_ddd_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT bioact_2_ref FOREIGN KEY (act_ref_id) REFERENCES public.reference(id);

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_ddi_risk FOREIGN KEY (ddi_risk, ddi_ref_id) REFERENCES public.ddi_risk(risk, ddi_ref_id);

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_drug_class1 FOREIGN KEY (drug_class1) REFERENCES public.drug_class(name);

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_drug_class2 FOREIGN KEY (drug_class2) REFERENCES public.drug_class(name);

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT doid_xref_2_doid FOREIGN KEY (doid) REFERENCES public.doid(doid);

ALTER TABLE ONLY public.faers
    ADD CONSTRAINT faers_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_2_idtype FOREIGN KEY (id_type) REFERENCES public.id_type(type);

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT moa_2_ref FOREIGN KEY (moa_ref_id) REFERENCES public.reference(id);

ALTER TABLE ONLY public.ob_exclusivity
    ADD CONSTRAINT obexcl_2_exclcode FOREIGN KEY (exclusivity_code) REFERENCES public.ob_exclusivity_code(code);

ALTER TABLE ONLY public.ob_patent
    ADD CONSTRAINT obpat_2_usecode FOREIGN KEY (patent_use_code) REFERENCES public.ob_patent_use_code(code);

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT obprod_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT omop_relationship_struct_id_fkey FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT pdb_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT pharma_class_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.pka
    ADD CONSTRAINT pka_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd_2_label FOREIGN KEY (label_id) REFERENCES public.label(id);

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd_2_prd FOREIGN KEY (ndc_product_code) REFERENCES public.product(ndc_product_code);

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT reference_2_reftype FOREIGN KEY (type) REFERENCES public.ref_type(type);

ALTER TABLE ONLY public.section
    ADD CONSTRAINT section_2_label FOREIGN KEY (label_id) REFERENCES public.label(id);

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_2_atc FOREIGN KEY (atc_code) REFERENCES public.atc(code);

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT struct2drgclass_2_drgclass FOREIGN KEY (drug_class_id) REFERENCES public.drug_class(id);

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT struct2drgclass_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT struct2parent_2_parent FOREIGN KEY (parent_id) REFERENCES public.parentmol(cd_id);

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT struct2parent_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT struct_2_obprod FOREIGN KEY (prod_id) REFERENCES public.ob_product(id);

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT struct_2_stem FOREIGN KEY (stem) REFERENCES public.inn_stem(stem);

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT structure_type_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT structure_type_2_type FOREIGN KEY (type) REFERENCES public.struct_type_def(type);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT synonym_2_parent FOREIGN KEY (parent_id) REFERENCES public.parentmol(cd_id);

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT synonym_2_struct FOREIGN KEY (id) REFERENCES public.structures(id);

ALTER TABLE ONLY public.td2tc
    ADD CONSTRAINT td2tc_2_td FOREIGN KEY (target_id) REFERENCES public.target_dictionary(id);

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT tdgo2tc_2_go FOREIGN KEY (go_id) REFERENCES public.target_go(id);

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT tdgo2tc_2_kw FOREIGN KEY (tdkey_id) REFERENCES public.target_keyword(id);

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT tdgo2tc_2_tc FOREIGN KEY (component_id) REFERENCES public.target_component(id);

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT tdgo2tc_2_tc FOREIGN KEY (component_id) REFERENCES public.target_component(id);
