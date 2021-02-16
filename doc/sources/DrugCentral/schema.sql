--
-- PostgreSQL database dump
--

-- Dumped from database version 12.4 (Ubuntu 12.4-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.4 (Ubuntu 12.4-0ubuntu0.20.04.1)

-- Started on 2020-09-18 10:48:22 MDT

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 202 (class 1259 OID 397905)
-- Name: act_table_full; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.act_table_full OWNER TO jjyang;

--
-- TOC entry 3683 (class 0 OID 0)
-- Dependencies: 202
-- Name: TABLE act_table_full; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.act_table_full IS 'bioactivity data aggregated from multiple resources';


--
-- TOC entry 203 (class 1259 OID 397911)
-- Name: action_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.action_type (
    id integer NOT NULL,
    action_type character varying(50) NOT NULL,
    description character varying(200) NOT NULL,
    parent_type character varying(50)
);


ALTER TABLE public.action_type OWNER TO jjyang;

--
-- TOC entry 3684 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE action_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.action_type IS 'drug modulatory action types';


--
-- TOC entry 204 (class 1259 OID 397914)
-- Name: action_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.action_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.action_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3685 (class 0 OID 0)
-- Dependencies: 204
-- Name: action_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.action_type_id_seq OWNED BY public.action_type.id;


--
-- TOC entry 205 (class 1259 OID 397916)
-- Name: active_ingredient; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.active_ingredient OWNER TO jjyang;

--
-- TOC entry 3686 (class 0 OID 0)
-- Dependencies: 205
-- Name: TABLE active_ingredient; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.active_ingredient IS 'active ingredients listed in FDA drug labels';


--
-- TOC entry 206 (class 1259 OID 397922)
-- Name: active_ingredient_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.active_ingredient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.active_ingredient_id_seq OWNER TO jjyang;

--
-- TOC entry 3687 (class 0 OID 0)
-- Dependencies: 206
-- Name: active_ingredient_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.active_ingredient_id_seq OWNED BY public.active_ingredient.id;


--
-- TOC entry 207 (class 1259 OID 397924)
-- Name: approval; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.approval (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    approval date,
    type character varying(200) NOT NULL,
    applicant character varying(100),
    orphan boolean
);


ALTER TABLE public.approval OWNER TO jjyang;

--
-- TOC entry 3688 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE approval; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.approval IS 'approval dates by drug regualtory agencies';


--
-- TOC entry 208 (class 1259 OID 397927)
-- Name: approval_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.approval_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.approval_id_seq OWNER TO jjyang;

--
-- TOC entry 3689 (class 0 OID 0)
-- Dependencies: 208
-- Name: approval_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.approval_id_seq OWNED BY public.approval.id;


--
-- TOC entry 209 (class 1259 OID 397929)
-- Name: approval_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.approval_type (
    id integer NOT NULL,
    descr character varying(200)
);


ALTER TABLE public.approval_type OWNER TO jjyang;

--
-- TOC entry 3690 (class 0 OID 0)
-- Dependencies: 209
-- Name: TABLE approval_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.approval_type IS 'listing of drug regulatory agencies';


--
-- TOC entry 210 (class 1259 OID 397932)
-- Name: approval_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.approval_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.approval_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3691 (class 0 OID 0)
-- Dependencies: 210
-- Name: approval_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.approval_type_id_seq OWNED BY public.approval_type.id;


--
-- TOC entry 211 (class 1259 OID 397934)
-- Name: atc; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.atc OWNER TO jjyang;

--
-- TOC entry 3692 (class 0 OID 0)
-- Dependencies: 211
-- Name: TABLE atc; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.atc IS 'WHO ATC codes';


--
-- TOC entry 212 (class 1259 OID 397940)
-- Name: atc_ddd; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.atc_ddd (
    id integer NOT NULL,
    atc_code character(7),
    ddd real NOT NULL,
    unit_type character varying(10),
    route character varying(20),
    comment character varying(100),
    struct_id integer NOT NULL
);


ALTER TABLE public.atc_ddd OWNER TO jjyang;

--
-- TOC entry 3693 (class 0 OID 0)
-- Dependencies: 212
-- Name: TABLE atc_ddd; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.atc_ddd IS 'WHO Defined Daily Dose, the DDD is the assumed average maintenance dose per day for a drug used for its main indication in adults';


--
-- TOC entry 213 (class 1259 OID 397943)
-- Name: atc_ddd_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.atc_ddd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.atc_ddd_id_seq OWNER TO jjyang;

--
-- TOC entry 3694 (class 0 OID 0)
-- Dependencies: 213
-- Name: atc_ddd_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.atc_ddd_id_seq OWNED BY public.atc_ddd.id;


--
-- TOC entry 214 (class 1259 OID 397945)
-- Name: atc_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.atc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.atc_id_seq OWNER TO jjyang;

--
-- TOC entry 3695 (class 0 OID 0)
-- Dependencies: 214
-- Name: atc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.atc_id_seq OWNED BY public.atc.id;


--
-- TOC entry 215 (class 1259 OID 397947)
-- Name: attr_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.attr_type (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(20) NOT NULL
);


ALTER TABLE public.attr_type OWNER TO jjyang;

--
-- TOC entry 3696 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE attr_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.attr_type IS 'listing of generic attribute types';


--
-- TOC entry 216 (class 1259 OID 397950)
-- Name: attr_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.attr_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.attr_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3697 (class 0 OID 0)
-- Dependencies: 216
-- Name: attr_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.attr_type_id_seq OWNED BY public.attr_type.id;


--
-- TOC entry 217 (class 1259 OID 397952)
-- Name: data_source; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.data_source (
    src_id smallint NOT NULL,
    source_name character varying(100)
);


ALTER TABLE public.data_source OWNER TO jjyang;

--
-- TOC entry 3698 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE data_source; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.data_source IS 'listing of datasources';


--
-- TOC entry 218 (class 1259 OID 397955)
-- Name: dbversion; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.dbversion (
    version bigint NOT NULL,
    dtime timestamp without time zone NOT NULL
);


ALTER TABLE public.dbversion OWNER TO jjyang;

--
-- TOC entry 3699 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE dbversion; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.dbversion IS 'current database version';


--
-- TOC entry 219 (class 1259 OID 397958)
-- Name: ddi; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ddi (
    id integer NOT NULL,
    drug_class1 character varying(500) NOT NULL,
    drug_class2 character varying(500) NOT NULL,
    ddi_ref_id integer NOT NULL,
    ddi_risk character varying(200) NOT NULL,
    description character varying(4000),
    source_id character varying(200)
);


ALTER TABLE public.ddi OWNER TO jjyang;

--
-- TOC entry 3700 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE ddi; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ddi IS 'Drug-Drug and Drug class - Drug class interaction table';


--
-- TOC entry 220 (class 1259 OID 397964)
-- Name: ddi_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ddi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ddi_id_seq OWNER TO jjyang;

--
-- TOC entry 3701 (class 0 OID 0)
-- Dependencies: 220
-- Name: ddi_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ddi_id_seq OWNED BY public.ddi.id;


--
-- TOC entry 221 (class 1259 OID 397966)
-- Name: ddi_risk; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ddi_risk (
    id integer NOT NULL,
    risk character varying(200) NOT NULL,
    ddi_ref_id integer NOT NULL
);


ALTER TABLE public.ddi_risk OWNER TO jjyang;

--
-- TOC entry 3702 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE ddi_risk; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ddi_risk IS 'Qualitative assesments of drug-drug interactions severity';


--
-- TOC entry 222 (class 1259 OID 397969)
-- Name: ddi_risk_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ddi_risk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ddi_risk_id_seq OWNER TO jjyang;

--
-- TOC entry 3703 (class 0 OID 0)
-- Dependencies: 222
-- Name: ddi_risk_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ddi_risk_id_seq OWNED BY public.ddi_risk.id;


--
-- TOC entry 223 (class 1259 OID 397971)
-- Name: doid; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.doid (
    id integer NOT NULL,
    label character varying(1000),
    doid character varying(50),
    url character varying(100)
);


ALTER TABLE public.doid OWNER TO jjyang;

--
-- TOC entry 3704 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE doid; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.doid IS 'listing on Disease-Ontology concepts';


--
-- TOC entry 224 (class 1259 OID 397977)
-- Name: doid_xref; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.doid_xref (
    id integer NOT NULL,
    doid character varying(50),
    source character varying(50),
    xref character varying(50)
);


ALTER TABLE public.doid_xref OWNER TO jjyang;

--
-- TOC entry 3705 (class 0 OID 0)
-- Dependencies: 224
-- Name: TABLE doid_xref; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.doid_xref IS 'Disease-Ontology terms mappings to external resources';


--
-- TOC entry 225 (class 1259 OID 397980)
-- Name: doid_xref_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.doid_xref_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.doid_xref_id_seq OWNER TO jjyang;

--
-- TOC entry 3706 (class 0 OID 0)
-- Dependencies: 225
-- Name: doid_xref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.doid_xref_id_seq OWNED BY public.doid_xref.id;


--
-- TOC entry 226 (class 1259 OID 397982)
-- Name: drug_class; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.drug_class (
    id integer NOT NULL,
    name character varying(500) NOT NULL,
    is_group smallint DEFAULT 0 NOT NULL,
    source character varying(100)
);


ALTER TABLE public.drug_class OWNER TO jjyang;

--
-- TOC entry 3707 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE drug_class; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.drug_class IS 'groupings of drugs used to derive Drug-Drug and Drug class - Drug class interactions';


--
-- TOC entry 227 (class 1259 OID 397989)
-- Name: drug_class_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.drug_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drug_class_id_seq OWNER TO jjyang;

--
-- TOC entry 3708 (class 0 OID 0)
-- Dependencies: 227
-- Name: drug_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.drug_class_id_seq OWNED BY public.drug_class.id;


--
-- TOC entry 228 (class 1259 OID 397991)
-- Name: faers; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.faers OWNER TO jjyang;

--
-- TOC entry 3709 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE faers; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.faers IS 'Adverse events from FDA FAERS database';


--
-- TOC entry 3710 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.llr; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.llr IS 'Likelihood Ratio based on method described in http://dx.doi.org/10.1198/jasa.2011.ap10243';


--
-- TOC entry 3711 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.llr_threshold; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.llr_threshold IS 'Likelihood Ratio threshold based on method described in http://dx.doi.org/10.1198/jasa.2011.ap10243';


--
-- TOC entry 3712 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.drug_ae; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.drug_ae IS 'number of patients taking drug and having adverse event';


--
-- TOC entry 3713 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.drug_no_ae; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.drug_no_ae IS 'number of patients taking drug and not having adverse event';


--
-- TOC entry 3714 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.no_drug_ae; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.no_drug_ae IS 'number of patients not taking drug and having adverse event';


--
-- TOC entry 3715 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN faers.no_drug_no_ae; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.faers.no_drug_no_ae IS 'number of patients not taking drug and not having adverse event';


--
-- TOC entry 229 (class 1259 OID 397994)
-- Name: faers_female; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.faers_female OWNER TO jjyang;

--
-- TOC entry 230 (class 1259 OID 397997)
-- Name: faers_female_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.faers_female_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.faers_female_id_seq OWNER TO jjyang;

--
-- TOC entry 3716 (class 0 OID 0)
-- Dependencies: 230
-- Name: faers_female_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.faers_female_id_seq OWNED BY public.faers_female.id;


--
-- TOC entry 231 (class 1259 OID 397999)
-- Name: faers_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.faers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.faers_id_seq OWNER TO jjyang;

--
-- TOC entry 3717 (class 0 OID 0)
-- Dependencies: 231
-- Name: faers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.faers_id_seq OWNED BY public.faers.id;


--
-- TOC entry 232 (class 1259 OID 398001)
-- Name: faers_male; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.faers_male OWNER TO jjyang;

--
-- TOC entry 233 (class 1259 OID 398004)
-- Name: faers_male_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.faers_male_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.faers_male_id_seq OWNER TO jjyang;

--
-- TOC entry 3718 (class 0 OID 0)
-- Dependencies: 233
-- Name: faers_male_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.faers_male_id_seq OWNED BY public.faers_male.id;


--
-- TOC entry 234 (class 1259 OID 398006)
-- Name: faers_top; Type: VIEW; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.faers_top OWNER TO jjyang;

--
-- TOC entry 235 (class 1259 OID 398011)
-- Name: id_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.id_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL,
    description character varying(500),
    url character varying(500)
);


ALTER TABLE public.id_type OWNER TO jjyang;

--
-- TOC entry 3719 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE id_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.id_type IS 'list external identifiers sources';


--
-- TOC entry 236 (class 1259 OID 398017)
-- Name: id_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.id_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.id_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3720 (class 0 OID 0)
-- Dependencies: 236
-- Name: id_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.id_type_id_seq OWNED BY public.id_type.id;


--
-- TOC entry 237 (class 1259 OID 398019)
-- Name: identifier; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.identifier (
    id integer NOT NULL,
    identifier character varying(50) NOT NULL,
    id_type character varying(50) NOT NULL,
    struct_id integer NOT NULL,
    parent_match boolean
);


ALTER TABLE public.identifier OWNER TO jjyang;

--
-- TOC entry 3721 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE identifier; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.identifier IS 'mapping to external drug resouces';


--
-- TOC entry 238 (class 1259 OID 398022)
-- Name: identifier_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.identifier_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.identifier_id_seq OWNER TO jjyang;

--
-- TOC entry 3722 (class 0 OID 0)
-- Dependencies: 238
-- Name: identifier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.identifier_id_seq OWNED BY public.identifier.id;


--
-- TOC entry 239 (class 1259 OID 398024)
-- Name: inn_stem; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.inn_stem (
    id integer NOT NULL,
    stem character varying(50),
    definition character varying(1000) NOT NULL,
    national_name character varying(20),
    length smallint,
    discontinued boolean
);


ALTER TABLE public.inn_stem OWNER TO jjyang;

--
-- TOC entry 3723 (class 0 OID 0)
-- Dependencies: 239
-- Name: TABLE inn_stem; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.inn_stem IS 'listing of WHO INN stems based on http://www.who.int/medicines/services/inn/stembook/en/';


--
-- TOC entry 240 (class 1259 OID 398030)
-- Name: inn_stem_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.inn_stem_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.inn_stem_id_seq OWNER TO jjyang;

--
-- TOC entry 3724 (class 0 OID 0)
-- Dependencies: 240
-- Name: inn_stem_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.inn_stem_id_seq OWNED BY public.inn_stem.id;


--
-- TOC entry 241 (class 1259 OID 398032)
-- Name: label; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.label (
    id character varying(50) NOT NULL,
    category character varying(100),
    title character varying(1000),
    effective_date date,
    assigned_entity character varying(500),
    pdf_url character varying(500)
);


ALTER TABLE public.label OWNER TO jjyang;

--
-- TOC entry 3725 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE label; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.label IS 'FDA drug labels SPL identifiers and categories';


--
-- TOC entry 242 (class 1259 OID 398038)
-- Name: lincs_signature; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.lincs_signature OWNER TO jjyang;

--
-- TOC entry 243 (class 1259 OID 398041)
-- Name: lincs_signature_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.lincs_signature_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.lincs_signature_id_seq OWNER TO jjyang;

--
-- TOC entry 3726 (class 0 OID 0)
-- Dependencies: 243
-- Name: lincs_signature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.lincs_signature_id_seq OWNED BY public.lincs_signature.id;


--
-- TOC entry 244 (class 1259 OID 398043)
-- Name: ob_exclusivity; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ob_exclusivity (
    id integer NOT NULL,
    appl_type character(1),
    appl_no character(6),
    product_no character(3),
    exclusivity_code character varying(10),
    exclusivity_date date
);


ALTER TABLE public.ob_exclusivity OWNER TO jjyang;

--
-- TOC entry 3727 (class 0 OID 0)
-- Dependencies: 244
-- Name: TABLE ob_exclusivity; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ob_exclusivity IS 'Exclusivity data for FDA Orange book pharmaceutical products';


--
-- TOC entry 245 (class 1259 OID 398046)
-- Name: ob_exclusivity_code; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ob_exclusivity_code (
    code character varying(10) NOT NULL,
    description character varying(500)
);


ALTER TABLE public.ob_exclusivity_code OWNER TO jjyang;

--
-- TOC entry 3728 (class 0 OID 0)
-- Dependencies: 245
-- Name: TABLE ob_exclusivity_code; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ob_exclusivity_code IS 'Exclusivity codes from FDA Orange book';


--
-- TOC entry 246 (class 1259 OID 398052)
-- Name: ob_exclusivity_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ob_exclusivity_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ob_exclusivity_id_seq OWNER TO jjyang;

--
-- TOC entry 3729 (class 0 OID 0)
-- Dependencies: 246
-- Name: ob_exclusivity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ob_exclusivity_id_seq OWNED BY public.ob_exclusivity.id;


--
-- TOC entry 247 (class 1259 OID 398054)
-- Name: ob_product; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.ob_product OWNER TO jjyang;

--
-- TOC entry 3730 (class 0 OID 0)
-- Dependencies: 247
-- Name: TABLE ob_product; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ob_product IS 'FDA Orange book pharmaceutical products';


--
-- TOC entry 248 (class 1259 OID 398060)
-- Name: struct2obprod; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.struct2obprod (
    struct_id integer NOT NULL,
    prod_id integer NOT NULL,
    strength character varying(4000)
);


ALTER TABLE public.struct2obprod OWNER TO jjyang;

--
-- TOC entry 249 (class 1259 OID 398066)
-- Name: ob_exclusivity_view; Type: VIEW; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.ob_exclusivity_view OWNER TO jjyang;

--
-- TOC entry 250 (class 1259 OID 398071)
-- Name: ob_patent; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.ob_patent OWNER TO jjyang;

--
-- TOC entry 3731 (class 0 OID 0)
-- Dependencies: 250
-- Name: TABLE ob_patent; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ob_patent IS 'Patent data for FDA Orange book pharmaceutical products';


--
-- TOC entry 251 (class 1259 OID 398074)
-- Name: ob_patent_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ob_patent_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ob_patent_id_seq OWNER TO jjyang;

--
-- TOC entry 3732 (class 0 OID 0)
-- Dependencies: 251
-- Name: ob_patent_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ob_patent_id_seq OWNED BY public.ob_patent.id;


--
-- TOC entry 252 (class 1259 OID 398076)
-- Name: ob_patent_use_code; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ob_patent_use_code (
    code character varying(10) NOT NULL,
    description character varying(500)
);


ALTER TABLE public.ob_patent_use_code OWNER TO jjyang;

--
-- TOC entry 3733 (class 0 OID 0)
-- Dependencies: 252
-- Name: TABLE ob_patent_use_code; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ob_patent_use_code IS 'Patent use codes from FDA Orange book';


--
-- TOC entry 253 (class 1259 OID 398082)
-- Name: ob_patent_view; Type: VIEW; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.ob_patent_view OWNER TO jjyang;

--
-- TOC entry 254 (class 1259 OID 398087)
-- Name: ob_product_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ob_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ob_product_id_seq OWNER TO jjyang;

--
-- TOC entry 3734 (class 0 OID 0)
-- Dependencies: 254
-- Name: ob_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ob_product_id_seq OWNED BY public.ob_product.id;


--
-- TOC entry 255 (class 1259 OID 398089)
-- Name: omop_relationship; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.omop_relationship OWNER TO jjyang;

--
-- TOC entry 3735 (class 0 OID 0)
-- Dependencies: 255
-- Name: TABLE omop_relationship; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.omop_relationship IS 'drug indications/contra-indications/off-label use based on OMOP v4 and manual annotations';


--
-- TOC entry 256 (class 1259 OID 398095)
-- Name: omop_relationship_doid_view; Type: VIEW; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.omop_relationship_doid_view OWNER TO jjyang;

--
-- TOC entry 257 (class 1259 OID 398100)
-- Name: omop_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.omop_relationship_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.omop_relationship_id_seq OWNER TO jjyang;

--
-- TOC entry 3736 (class 0 OID 0)
-- Dependencies: 257
-- Name: omop_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.omop_relationship_id_seq OWNED BY public.omop_relationship.id;


--
-- TOC entry 258 (class 1259 OID 398102)
-- Name: parentmol; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.parentmol OWNER TO jjyang;

--
-- TOC entry 3737 (class 0 OID 0)
-- Dependencies: 258
-- Name: TABLE parentmol; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.parentmol IS 'parent drug molecules for active ingredients formulated as prodrugs';


--
-- TOC entry 259 (class 1259 OID 398108)
-- Name: parentmol_cd_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.parentmol_cd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parentmol_cd_id_seq OWNER TO jjyang;

--
-- TOC entry 3738 (class 0 OID 0)
-- Dependencies: 259
-- Name: parentmol_cd_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.parentmol_cd_id_seq OWNED BY public.parentmol.cd_id;


--
-- TOC entry 260 (class 1259 OID 398110)
-- Name: pdb; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.pdb OWNER TO jjyang;

--
-- TOC entry 3739 (class 0 OID 0)
-- Dependencies: 260
-- Name: TABLE pdb; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.pdb IS 'mapping to PDB protein-drug complexes';


--
-- TOC entry 261 (class 1259 OID 398116)
-- Name: pdb_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.pdb_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pdb_id_seq OWNER TO jjyang;

--
-- TOC entry 3740 (class 0 OID 0)
-- Dependencies: 261
-- Name: pdb_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.pdb_id_seq OWNED BY public.pdb.id;


--
-- TOC entry 262 (class 1259 OID 398118)
-- Name: pharma_class; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.pharma_class (
    id integer NOT NULL,
    struct_id integer,
    type character varying(20) NOT NULL,
    name character varying(1000) NOT NULL,
    class_code character varying(20),
    source character varying(100)
);


ALTER TABLE public.pharma_class OWNER TO jjyang;

--
-- TOC entry 3741 (class 0 OID 0)
-- Dependencies: 262
-- Name: TABLE pharma_class; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.pharma_class IS 'pharmacologic classifications of drugs from multiple resources';


--
-- TOC entry 263 (class 1259 OID 398124)
-- Name: pharma_class_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.pharma_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pharma_class_id_seq OWNER TO jjyang;

--
-- TOC entry 3742 (class 0 OID 0)
-- Dependencies: 263
-- Name: pharma_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.pharma_class_id_seq OWNED BY public.pharma_class.id;


--
-- TOC entry 264 (class 1259 OID 398126)
-- Name: pka; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.pka (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    pka_level character varying(5),
    value double precision,
    pka_type character(1) NOT NULL
);


ALTER TABLE public.pka OWNER TO jjyang;

--
-- TOC entry 3743 (class 0 OID 0)
-- Dependencies: 264
-- Name: TABLE pka; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.pka IS 'logarithm of acid dissociation constant calculated using MoKa 3.0.0';


--
-- TOC entry 265 (class 1259 OID 398129)
-- Name: pka_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.pka_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pka_id_seq OWNER TO jjyang;

--
-- TOC entry 3744 (class 0 OID 0)
-- Dependencies: 265
-- Name: pka_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.pka_id_seq OWNED BY public.pka.id;


--
-- TOC entry 266 (class 1259 OID 398131)
-- Name: prd2label; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.prd2label (
    ndc_product_code character varying(20) NOT NULL,
    label_id character varying(50) NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.prd2label OWNER TO jjyang;

--
-- TOC entry 3745 (class 0 OID 0)
-- Dependencies: 266
-- Name: TABLE prd2label; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.prd2label IS 'mappings between FDA drug labels and pharmaceutical products associated with these labels';


--
-- TOC entry 267 (class 1259 OID 398134)
-- Name: prd2label_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.prd2label_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prd2label_id_seq OWNER TO jjyang;

--
-- TOC entry 3746 (class 0 OID 0)
-- Dependencies: 267
-- Name: prd2label_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.prd2label_id_seq OWNED BY public.prd2label.id;


--
-- TOC entry 268 (class 1259 OID 398136)
-- Name: product; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.product OWNER TO jjyang;

--
-- TOC entry 3747 (class 0 OID 0)
-- Dependencies: 268
-- Name: TABLE product; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.product IS 'pharmaceutical products associated with FDA drug labels';


--
-- TOC entry 269 (class 1259 OID 398142)
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_id_seq OWNER TO jjyang;

--
-- TOC entry 3748 (class 0 OID 0)
-- Dependencies: 269
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- TOC entry 270 (class 1259 OID 398144)
-- Name: property; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.property (
    id integer NOT NULL,
    property_type_id integer,
    property_type_symbol character varying(10) NOT NULL,
    struct_id integer,
    value double precision,
    reference_id integer,
    reference_type character varying(50),
    source character varying(80)
);


ALTER TABLE public.property OWNER TO jjyang;

--
-- TOC entry 271 (class 1259 OID 398147)
-- Name: property_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.property_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_id_seq OWNER TO jjyang;

--
-- TOC entry 3749 (class 0 OID 0)
-- Dependencies: 271
-- Name: property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.property_id_seq OWNED BY public.property.id;


--
-- TOC entry 272 (class 1259 OID 398149)
-- Name: property_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.property_type (
    id integer NOT NULL,
    category character varying(20),
    name character varying(80),
    symbol character varying(10),
    units character varying(10)
);


ALTER TABLE public.property_type OWNER TO jjyang;

--
-- TOC entry 273 (class 1259 OID 398152)
-- Name: property_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.property_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3750 (class 0 OID 0)
-- Dependencies: 273
-- Name: property_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.property_type_id_seq OWNED BY public.property_type.id;


--
-- TOC entry 274 (class 1259 OID 398154)
-- Name: protein_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.protein_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL
);


ALTER TABLE public.protein_type OWNER TO jjyang;

--
-- TOC entry 3751 (class 0 OID 0)
-- Dependencies: 274
-- Name: TABLE protein_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.protein_type IS 'simple classification of protein types interacting with drugs';


--
-- TOC entry 275 (class 1259 OID 398157)
-- Name: protein_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.protein_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.protein_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3752 (class 0 OID 0)
-- Dependencies: 275
-- Name: protein_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.protein_type_id_seq OWNED BY public.protein_type.id;


--
-- TOC entry 276 (class 1259 OID 398159)
-- Name: ref_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.ref_type (
    id integer NOT NULL,
    type character varying(50) NOT NULL
);


ALTER TABLE public.ref_type OWNER TO jjyang;

--
-- TOC entry 3753 (class 0 OID 0)
-- Dependencies: 276
-- Name: TABLE ref_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.ref_type IS 'listing of reference types';


--
-- TOC entry 277 (class 1259 OID 398162)
-- Name: ref_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.ref_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ref_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3754 (class 0 OID 0)
-- Dependencies: 277
-- Name: ref_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.ref_type_id_seq OWNED BY public.ref_type.id;


--
-- TOC entry 278 (class 1259 OID 398164)
-- Name: reference; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.reference OWNER TO jjyang;

--
-- TOC entry 3755 (class 0 OID 0)
-- Dependencies: 278
-- Name: TABLE reference; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.reference IS 'external references for drug bioactivities and mechanism of action';


--
-- TOC entry 279 (class 1259 OID 398170)
-- Name: reference_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.reference_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reference_id_seq OWNER TO jjyang;

--
-- TOC entry 3756 (class 0 OID 0)
-- Dependencies: 279
-- Name: reference_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.reference_id_seq OWNED BY public.reference.id;


--
-- TOC entry 280 (class 1259 OID 398172)
-- Name: section; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.section (
    id integer NOT NULL,
    text text,
    label_id character varying(50),
    code character varying(20),
    title character varying(4000)
);


ALTER TABLE public.section OWNER TO jjyang;

--
-- TOC entry 3757 (class 0 OID 0)
-- Dependencies: 280
-- Name: TABLE section; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.section IS 'FDA SPL drug label sections';


--
-- TOC entry 281 (class 1259 OID 398178)
-- Name: section_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.section_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.section_id_seq OWNER TO jjyang;

--
-- TOC entry 3758 (class 0 OID 0)
-- Dependencies: 281
-- Name: section_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.section_id_seq OWNED BY public.section.id;


--
-- TOC entry 282 (class 1259 OID 398180)
-- Name: struct2atc; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.struct2atc (
    struct_id integer NOT NULL,
    atc_code character(7) NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.struct2atc OWNER TO jjyang;

--
-- TOC entry 3759 (class 0 OID 0)
-- Dependencies: 282
-- Name: TABLE struct2atc; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.struct2atc IS 'mapping between structures table and WHO ATC codes';


--
-- TOC entry 283 (class 1259 OID 398183)
-- Name: struct2atc_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.struct2atc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.struct2atc_id_seq OWNER TO jjyang;

--
-- TOC entry 3760 (class 0 OID 0)
-- Dependencies: 283
-- Name: struct2atc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.struct2atc_id_seq OWNED BY public.struct2atc.id;


--
-- TOC entry 284 (class 1259 OID 398185)
-- Name: struct2drgclass; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.struct2drgclass (
    id integer NOT NULL,
    struct_id integer NOT NULL,
    drug_class_id integer NOT NULL
);


ALTER TABLE public.struct2drgclass OWNER TO jjyang;

--
-- TOC entry 3761 (class 0 OID 0)
-- Dependencies: 284
-- Name: TABLE struct2drgclass; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.struct2drgclass IS 'mapping between structures and drug_class tables';


--
-- TOC entry 285 (class 1259 OID 398188)
-- Name: struct2drgclass_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.struct2drgclass_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.struct2drgclass_id_seq OWNER TO jjyang;

--
-- TOC entry 3762 (class 0 OID 0)
-- Dependencies: 285
-- Name: struct2drgclass_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.struct2drgclass_id_seq OWNED BY public.struct2drgclass.id;


--
-- TOC entry 286 (class 1259 OID 398190)
-- Name: struct2parent; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.struct2parent (
    struct_id integer NOT NULL,
    parent_id integer NOT NULL
);


ALTER TABLE public.struct2parent OWNER TO jjyang;

--
-- TOC entry 3763 (class 0 OID 0)
-- Dependencies: 286
-- Name: TABLE struct2parent; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.struct2parent IS 'mapping between prodrugs in structures table and active parent molecules';


--
-- TOC entry 287 (class 1259 OID 398193)
-- Name: struct_type_def; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.struct_type_def (
    id integer NOT NULL,
    type character varying(50),
    description character varying(200)
);


ALTER TABLE public.struct_type_def OWNER TO jjyang;

--
-- TOC entry 3764 (class 0 OID 0)
-- Dependencies: 287
-- Name: TABLE struct_type_def; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.struct_type_def IS 'simple classification of chemical entities in structures table';


--
-- TOC entry 288 (class 1259 OID 398196)
-- Name: struct_type_def_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.struct_type_def_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.struct_type_def_id_seq OWNER TO jjyang;

--
-- TOC entry 3765 (class 0 OID 0)
-- Dependencies: 288
-- Name: struct_type_def_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.struct_type_def_id_seq OWNED BY public.struct_type_def.id;


--
-- TOC entry 289 (class 1259 OID 398198)
-- Name: structure_type; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.structure_type (
    id integer NOT NULL,
    struct_id integer,
    type character varying(50) DEFAULT 'UNKNOWN'::character varying NOT NULL
);


ALTER TABLE public.structure_type OWNER TO jjyang;

--
-- TOC entry 3766 (class 0 OID 0)
-- Dependencies: 289
-- Name: TABLE structure_type; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.structure_type IS 'mapping between chemical entities in structures table and types defined in struct_type_def';


--
-- TOC entry 290 (class 1259 OID 398202)
-- Name: structure_type_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.structure_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.structure_type_id_seq OWNER TO jjyang;

--
-- TOC entry 3767 (class 0 OID 0)
-- Dependencies: 290
-- Name: structure_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.structure_type_id_seq OWNED BY public.structure_type.id;


--
-- TOC entry 291 (class 1259 OID 398204)
-- Name: structures; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.structures OWNER TO jjyang;

--
-- TOC entry 3768 (class 0 OID 0)
-- Dependencies: 291
-- Name: TABLE structures; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.structures IS 'chemical entities in active pharmaceutical ingredients table';


--
-- TOC entry 3769 (class 0 OID 0)
-- Dependencies: 291
-- Name: COLUMN structures.rgb; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON COLUMN public.structures.rgb IS 'number of rigid bonds';


--
-- TOC entry 292 (class 1259 OID 398211)
-- Name: structures_cd_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.structures_cd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.structures_cd_id_seq OWNER TO jjyang;

--
-- TOC entry 3770 (class 0 OID 0)
-- Dependencies: 292
-- Name: structures_cd_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.structures_cd_id_seq OWNED BY public.structures.cd_id;


--
-- TOC entry 293 (class 1259 OID 398213)
-- Name: synonyms; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.synonyms (
    syn_id integer NOT NULL,
    id integer,
    name character varying(250) NOT NULL,
    preferred_name smallint,
    parent_id integer,
    lname character varying(250) DEFAULT 'GENERATED ALWAYS AS ( LCASE(NAME) )'::character varying
);


ALTER TABLE public.synonyms OWNER TO jjyang;

--
-- TOC entry 3771 (class 0 OID 0)
-- Dependencies: 293
-- Name: TABLE synonyms; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.synonyms IS 'unamiguous list of synonyms assigned to chemical entities in structures and parentmol tables';


--
-- TOC entry 294 (class 1259 OID 398220)
-- Name: synonyms_syn_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.synonyms_syn_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.synonyms_syn_id_seq OWNER TO jjyang;

--
-- TOC entry 3772 (class 0 OID 0)
-- Dependencies: 294
-- Name: synonyms_syn_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.synonyms_syn_id_seq OWNED BY public.synonyms.syn_id;


--
-- TOC entry 295 (class 1259 OID 398222)
-- Name: target_class; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.target_class (
    l1 character varying(50) NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.target_class OWNER TO jjyang;

--
-- TOC entry 3773 (class 0 OID 0)
-- Dependencies: 295
-- Name: TABLE target_class; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.target_class IS 'ChEMBL-db target classification system, level 1 only';


--
-- TOC entry 296 (class 1259 OID 398225)
-- Name: target_class_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.target_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_class_id_seq OWNER TO jjyang;

--
-- TOC entry 3774 (class 0 OID 0)
-- Dependencies: 296
-- Name: target_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.target_class_id_seq OWNED BY public.target_class.id;


--
-- TOC entry 297 (class 1259 OID 398227)
-- Name: target_component; Type: TABLE; Schema: public; Owner: jjyang
--

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


ALTER TABLE public.target_component OWNER TO jjyang;

--
-- TOC entry 3775 (class 0 OID 0)
-- Dependencies: 297
-- Name: TABLE target_component; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.target_component IS 'protein components of taregt interacting with drugs';


--
-- TOC entry 298 (class 1259 OID 398230)
-- Name: target_component_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.target_component_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_component_id_seq OWNER TO jjyang;

--
-- TOC entry 3776 (class 0 OID 0)
-- Dependencies: 298
-- Name: target_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.target_component_id_seq OWNED BY public.target_component.id;


--
-- TOC entry 299 (class 1259 OID 398232)
-- Name: target_dictionary; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.target_dictionary (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    target_class character varying(50) DEFAULT 'Unclassified'::character varying NOT NULL,
    protein_components smallint DEFAULT 0 NOT NULL,
    protein_type character varying(50),
    tdl character varying(500)
);


ALTER TABLE public.target_dictionary OWNER TO jjyang;

--
-- TOC entry 3777 (class 0 OID 0)
-- Dependencies: 299
-- Name: TABLE target_dictionary; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.target_dictionary IS 'target entities interacting with drugs';


--
-- TOC entry 300 (class 1259 OID 398240)
-- Name: target_dictionary_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.target_dictionary_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_dictionary_id_seq OWNER TO jjyang;

--
-- TOC entry 3778 (class 0 OID 0)
-- Dependencies: 300
-- Name: target_dictionary_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.target_dictionary_id_seq OWNED BY public.target_dictionary.id;


--
-- TOC entry 301 (class 1259 OID 398242)
-- Name: target_go; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.target_go (
    id character(10) NOT NULL,
    term character varying(200),
    type character(1)
);


ALTER TABLE public.target_go OWNER TO jjyang;

--
-- TOC entry 3779 (class 0 OID 0)
-- Dependencies: 301
-- Name: TABLE target_go; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.target_go IS 'Gene Ontology terms';


--
-- TOC entry 302 (class 1259 OID 398245)
-- Name: target_keyword; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.target_keyword (
    id character(7) NOT NULL,
    descr character varying(4000),
    category character varying(50),
    keyword character varying(200)
);


ALTER TABLE public.target_keyword OWNER TO jjyang;

--
-- TOC entry 3780 (class 0 OID 0)
-- Dependencies: 302
-- Name: TABLE target_keyword; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.target_keyword IS 'keywords extracted from Unirpot protein entries';


--
-- TOC entry 303 (class 1259 OID 398251)
-- Name: td2tc; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.td2tc (
    target_id integer NOT NULL,
    component_id integer NOT NULL
);


ALTER TABLE public.td2tc OWNER TO jjyang;

--
-- TOC entry 3781 (class 0 OID 0)
-- Dependencies: 303
-- Name: TABLE td2tc; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.td2tc IS 'mapping between drug target entities and protein components';


--
-- TOC entry 304 (class 1259 OID 398254)
-- Name: tdgo2tc; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.tdgo2tc (
    id integer NOT NULL,
    go_id character(10) NOT NULL,
    component_id integer
);


ALTER TABLE public.tdgo2tc OWNER TO jjyang;

--
-- TOC entry 3782 (class 0 OID 0)
-- Dependencies: 304
-- Name: TABLE tdgo2tc; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.tdgo2tc IS 'mapping between protein components and GO terms';


--
-- TOC entry 305 (class 1259 OID 398257)
-- Name: tdgo2tc_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.tdgo2tc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tdgo2tc_id_seq OWNER TO jjyang;

--
-- TOC entry 3783 (class 0 OID 0)
-- Dependencies: 305
-- Name: tdgo2tc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.tdgo2tc_id_seq OWNED BY public.tdgo2tc.id;


--
-- TOC entry 306 (class 1259 OID 398259)
-- Name: tdkey2tc; Type: TABLE; Schema: public; Owner: jjyang
--

CREATE TABLE public.tdkey2tc (
    id integer NOT NULL,
    tdkey_id character(7) NOT NULL,
    component_id integer
);


ALTER TABLE public.tdkey2tc OWNER TO jjyang;

--
-- TOC entry 3784 (class 0 OID 0)
-- Dependencies: 306
-- Name: TABLE tdkey2tc; Type: COMMENT; Schema: public; Owner: jjyang
--

COMMENT ON TABLE public.tdkey2tc IS 'mapping between protein components and Uniprot keywords';


--
-- TOC entry 307 (class 1259 OID 398262)
-- Name: tdkey2tc_id_seq; Type: SEQUENCE; Schema: public; Owner: jjyang
--

CREATE SEQUENCE public.tdkey2tc_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tdkey2tc_id_seq OWNER TO jjyang;

--
-- TOC entry 3785 (class 0 OID 0)
-- Dependencies: 307
-- Name: tdkey2tc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jjyang
--

ALTER SEQUENCE public.tdkey2tc_id_seq OWNED BY public.tdkey2tc.id;


--
-- TOC entry 3146 (class 2604 OID 398264)
-- Name: action_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.action_type ALTER COLUMN id SET DEFAULT nextval('public.action_type_id_seq'::regclass);


--
-- TOC entry 3147 (class 2604 OID 398265)
-- Name: active_ingredient id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.active_ingredient ALTER COLUMN id SET DEFAULT nextval('public.active_ingredient_id_seq'::regclass);


--
-- TOC entry 3148 (class 2604 OID 398266)
-- Name: approval id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval ALTER COLUMN id SET DEFAULT nextval('public.approval_id_seq'::regclass);


--
-- TOC entry 3149 (class 2604 OID 398267)
-- Name: approval_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval_type ALTER COLUMN id SET DEFAULT nextval('public.approval_type_id_seq'::regclass);


--
-- TOC entry 3150 (class 2604 OID 398268)
-- Name: atc id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc ALTER COLUMN id SET DEFAULT nextval('public.atc_id_seq'::regclass);


--
-- TOC entry 3151 (class 2604 OID 398269)
-- Name: atc_ddd id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc_ddd ALTER COLUMN id SET DEFAULT nextval('public.atc_ddd_id_seq'::regclass);


--
-- TOC entry 3152 (class 2604 OID 398270)
-- Name: attr_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.attr_type ALTER COLUMN id SET DEFAULT nextval('public.attr_type_id_seq'::regclass);


--
-- TOC entry 3153 (class 2604 OID 398271)
-- Name: ddi id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi ALTER COLUMN id SET DEFAULT nextval('public.ddi_id_seq'::regclass);


--
-- TOC entry 3154 (class 2604 OID 398272)
-- Name: ddi_risk id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi_risk ALTER COLUMN id SET DEFAULT nextval('public.ddi_risk_id_seq'::regclass);


--
-- TOC entry 3155 (class 2604 OID 398273)
-- Name: doid_xref id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid_xref ALTER COLUMN id SET DEFAULT nextval('public.doid_xref_id_seq'::regclass);


--
-- TOC entry 3157 (class 2604 OID 398274)
-- Name: drug_class id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.drug_class ALTER COLUMN id SET DEFAULT nextval('public.drug_class_id_seq'::regclass);


--
-- TOC entry 3158 (class 2604 OID 398275)
-- Name: faers id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers ALTER COLUMN id SET DEFAULT nextval('public.faers_id_seq'::regclass);


--
-- TOC entry 3159 (class 2604 OID 398276)
-- Name: faers_female id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers_female ALTER COLUMN id SET DEFAULT nextval('public.faers_female_id_seq'::regclass);


--
-- TOC entry 3160 (class 2604 OID 398277)
-- Name: faers_male id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers_male ALTER COLUMN id SET DEFAULT nextval('public.faers_male_id_seq'::regclass);


--
-- TOC entry 3161 (class 2604 OID 398278)
-- Name: id_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.id_type ALTER COLUMN id SET DEFAULT nextval('public.id_type_id_seq'::regclass);


--
-- TOC entry 3162 (class 2604 OID 398279)
-- Name: identifier id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.identifier ALTER COLUMN id SET DEFAULT nextval('public.identifier_id_seq'::regclass);


--
-- TOC entry 3163 (class 2604 OID 398280)
-- Name: inn_stem id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.inn_stem ALTER COLUMN id SET DEFAULT nextval('public.inn_stem_id_seq'::regclass);


--
-- TOC entry 3164 (class 2604 OID 398281)
-- Name: lincs_signature id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.lincs_signature ALTER COLUMN id SET DEFAULT nextval('public.lincs_signature_id_seq'::regclass);


--
-- TOC entry 3165 (class 2604 OID 398282)
-- Name: ob_exclusivity id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_exclusivity ALTER COLUMN id SET DEFAULT nextval('public.ob_exclusivity_id_seq'::regclass);


--
-- TOC entry 3167 (class 2604 OID 398283)
-- Name: ob_patent id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_patent ALTER COLUMN id SET DEFAULT nextval('public.ob_patent_id_seq'::regclass);


--
-- TOC entry 3166 (class 2604 OID 398284)
-- Name: ob_product id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_product ALTER COLUMN id SET DEFAULT nextval('public.ob_product_id_seq'::regclass);


--
-- TOC entry 3168 (class 2604 OID 398285)
-- Name: omop_relationship id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.omop_relationship ALTER COLUMN id SET DEFAULT nextval('public.omop_relationship_id_seq'::regclass);


--
-- TOC entry 3169 (class 2604 OID 398286)
-- Name: parentmol cd_id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.parentmol ALTER COLUMN cd_id SET DEFAULT nextval('public.parentmol_cd_id_seq'::regclass);


--
-- TOC entry 3170 (class 2604 OID 398287)
-- Name: pdb id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pdb ALTER COLUMN id SET DEFAULT nextval('public.pdb_id_seq'::regclass);


--
-- TOC entry 3171 (class 2604 OID 398288)
-- Name: pharma_class id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pharma_class ALTER COLUMN id SET DEFAULT nextval('public.pharma_class_id_seq'::regclass);


--
-- TOC entry 3172 (class 2604 OID 398289)
-- Name: pka id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pka ALTER COLUMN id SET DEFAULT nextval('public.pka_id_seq'::regclass);


--
-- TOC entry 3173 (class 2604 OID 398290)
-- Name: prd2label id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.prd2label ALTER COLUMN id SET DEFAULT nextval('public.prd2label_id_seq'::regclass);


--
-- TOC entry 3174 (class 2604 OID 398291)
-- Name: product id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- TOC entry 3175 (class 2604 OID 398292)
-- Name: property id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.property ALTER COLUMN id SET DEFAULT nextval('public.property_id_seq'::regclass);


--
-- TOC entry 3176 (class 2604 OID 398293)
-- Name: property_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.property_type ALTER COLUMN id SET DEFAULT nextval('public.property_type_id_seq'::regclass);


--
-- TOC entry 3177 (class 2604 OID 398294)
-- Name: protein_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.protein_type ALTER COLUMN id SET DEFAULT nextval('public.protein_type_id_seq'::regclass);


--
-- TOC entry 3178 (class 2604 OID 398295)
-- Name: ref_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ref_type ALTER COLUMN id SET DEFAULT nextval('public.ref_type_id_seq'::regclass);


--
-- TOC entry 3179 (class 2604 OID 398296)
-- Name: reference id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference ALTER COLUMN id SET DEFAULT nextval('public.reference_id_seq'::regclass);


--
-- TOC entry 3180 (class 2604 OID 398297)
-- Name: section id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.section ALTER COLUMN id SET DEFAULT nextval('public.section_id_seq'::regclass);


--
-- TOC entry 3181 (class 2604 OID 398298)
-- Name: struct2atc id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2atc ALTER COLUMN id SET DEFAULT nextval('public.struct2atc_id_seq'::regclass);


--
-- TOC entry 3182 (class 2604 OID 398299)
-- Name: struct2drgclass id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2drgclass ALTER COLUMN id SET DEFAULT nextval('public.struct2drgclass_id_seq'::regclass);


--
-- TOC entry 3183 (class 2604 OID 398300)
-- Name: struct_type_def id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct_type_def ALTER COLUMN id SET DEFAULT nextval('public.struct_type_def_id_seq'::regclass);


--
-- TOC entry 3185 (class 2604 OID 398301)
-- Name: structure_type id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structure_type ALTER COLUMN id SET DEFAULT nextval('public.structure_type_id_seq'::regclass);


--
-- TOC entry 3187 (class 2604 OID 398302)
-- Name: structures cd_id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structures ALTER COLUMN cd_id SET DEFAULT nextval('public.structures_cd_id_seq'::regclass);


--
-- TOC entry 3189 (class 2604 OID 398303)
-- Name: synonyms syn_id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms ALTER COLUMN syn_id SET DEFAULT nextval('public.synonyms_syn_id_seq'::regclass);


--
-- TOC entry 3190 (class 2604 OID 398304)
-- Name: target_class id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_class ALTER COLUMN id SET DEFAULT nextval('public.target_class_id_seq'::regclass);


--
-- TOC entry 3191 (class 2604 OID 398305)
-- Name: target_component id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_component ALTER COLUMN id SET DEFAULT nextval('public.target_component_id_seq'::regclass);


--
-- TOC entry 3194 (class 2604 OID 398306)
-- Name: target_dictionary id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_dictionary ALTER COLUMN id SET DEFAULT nextval('public.target_dictionary_id_seq'::regclass);


--
-- TOC entry 3195 (class 2604 OID 398307)
-- Name: tdgo2tc id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdgo2tc ALTER COLUMN id SET DEFAULT nextval('public.tdgo2tc_id_seq'::regclass);


--
-- TOC entry 3196 (class 2604 OID 398308)
-- Name: tdkey2tc id; Type: DEFAULT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdkey2tc ALTER COLUMN id SET DEFAULT nextval('public.tdkey2tc_id_seq'::regclass);

--
-- TOC entry 3786 (class 0 OID 0)
-- Dependencies: 204
-- Name: action_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.action_type_id_seq', 1, false);


--
-- TOC entry 3787 (class 0 OID 0)
-- Dependencies: 206
-- Name: active_ingredient_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.active_ingredient_id_seq', 1, false);


--
-- TOC entry 3788 (class 0 OID 0)
-- Dependencies: 208
-- Name: approval_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.approval_id_seq', 1, false);


--
-- TOC entry 3789 (class 0 OID 0)
-- Dependencies: 210
-- Name: approval_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.approval_type_id_seq', 1, false);


--
-- TOC entry 3790 (class 0 OID 0)
-- Dependencies: 213
-- Name: atc_ddd_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.atc_ddd_id_seq', 1, false);


--
-- TOC entry 3791 (class 0 OID 0)
-- Dependencies: 214
-- Name: atc_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.atc_id_seq', 1, false);


--
-- TOC entry 3792 (class 0 OID 0)
-- Dependencies: 216
-- Name: attr_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.attr_type_id_seq', 1, false);


--
-- TOC entry 3793 (class 0 OID 0)
-- Dependencies: 220
-- Name: ddi_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ddi_id_seq', 1, false);


--
-- TOC entry 3794 (class 0 OID 0)
-- Dependencies: 222
-- Name: ddi_risk_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ddi_risk_id_seq', 1, false);


--
-- TOC entry 3795 (class 0 OID 0)
-- Dependencies: 225
-- Name: doid_xref_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.doid_xref_id_seq', 1, false);


--
-- TOC entry 3796 (class 0 OID 0)
-- Dependencies: 227
-- Name: drug_class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.drug_class_id_seq', 1, false);


--
-- TOC entry 3797 (class 0 OID 0)
-- Dependencies: 230
-- Name: faers_female_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.faers_female_id_seq', 1, false);


--
-- TOC entry 3798 (class 0 OID 0)
-- Dependencies: 231
-- Name: faers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.faers_id_seq', 1, false);


--
-- TOC entry 3799 (class 0 OID 0)
-- Dependencies: 233
-- Name: faers_male_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.faers_male_id_seq', 1, false);


--
-- TOC entry 3800 (class 0 OID 0)
-- Dependencies: 236
-- Name: id_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.id_type_id_seq', 1, false);


--
-- TOC entry 3801 (class 0 OID 0)
-- Dependencies: 238
-- Name: identifier_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.identifier_id_seq', 1, false);


--
-- TOC entry 3802 (class 0 OID 0)
-- Dependencies: 240
-- Name: inn_stem_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.inn_stem_id_seq', 1, false);


--
-- TOC entry 3803 (class 0 OID 0)
-- Dependencies: 243
-- Name: lincs_signature_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.lincs_signature_id_seq', 1, false);


--
-- TOC entry 3804 (class 0 OID 0)
-- Dependencies: 246
-- Name: ob_exclusivity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ob_exclusivity_id_seq', 1, false);


--
-- TOC entry 3805 (class 0 OID 0)
-- Dependencies: 251
-- Name: ob_patent_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ob_patent_id_seq', 1, false);


--
-- TOC entry 3806 (class 0 OID 0)
-- Dependencies: 254
-- Name: ob_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ob_product_id_seq', 1, false);


--
-- TOC entry 3807 (class 0 OID 0)
-- Dependencies: 257
-- Name: omop_relationship_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.omop_relationship_id_seq', 1, false);


--
-- TOC entry 3808 (class 0 OID 0)
-- Dependencies: 259
-- Name: parentmol_cd_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.parentmol_cd_id_seq', 1, false);


--
-- TOC entry 3809 (class 0 OID 0)
-- Dependencies: 261
-- Name: pdb_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.pdb_id_seq', 1, false);


--
-- TOC entry 3810 (class 0 OID 0)
-- Dependencies: 263
-- Name: pharma_class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.pharma_class_id_seq', 1, false);


--
-- TOC entry 3811 (class 0 OID 0)
-- Dependencies: 265
-- Name: pka_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.pka_id_seq', 1, false);


--
-- TOC entry 3812 (class 0 OID 0)
-- Dependencies: 267
-- Name: prd2label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.prd2label_id_seq', 110577, true);


--
-- TOC entry 3813 (class 0 OID 0)
-- Dependencies: 269
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.product_id_seq', 1, false);


--
-- TOC entry 3814 (class 0 OID 0)
-- Dependencies: 271
-- Name: property_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.property_id_seq', 1, false);


--
-- TOC entry 3815 (class 0 OID 0)
-- Dependencies: 273
-- Name: property_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.property_type_id_seq', 1, false);


--
-- TOC entry 3816 (class 0 OID 0)
-- Dependencies: 275
-- Name: protein_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.protein_type_id_seq', 1, false);


--
-- TOC entry 3817 (class 0 OID 0)
-- Dependencies: 277
-- Name: ref_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.ref_type_id_seq', 1, false);


--
-- TOC entry 3818 (class 0 OID 0)
-- Dependencies: 279
-- Name: reference_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.reference_id_seq', 1, false);


--
-- TOC entry 3819 (class 0 OID 0)
-- Dependencies: 281
-- Name: section_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.section_id_seq', 1, false);


--
-- TOC entry 3820 (class 0 OID 0)
-- Dependencies: 283
-- Name: struct2atc_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.struct2atc_id_seq', 4817, true);


--
-- TOC entry 3821 (class 0 OID 0)
-- Dependencies: 285
-- Name: struct2drgclass_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.struct2drgclass_id_seq', 1, false);


--
-- TOC entry 3822 (class 0 OID 0)
-- Dependencies: 288
-- Name: struct_type_def_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.struct_type_def_id_seq', 1, false);


--
-- TOC entry 3823 (class 0 OID 0)
-- Dependencies: 290
-- Name: structure_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.structure_type_id_seq', 1, false);


--
-- TOC entry 3824 (class 0 OID 0)
-- Dependencies: 292
-- Name: structures_cd_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.structures_cd_id_seq', 1, false);


--
-- TOC entry 3825 (class 0 OID 0)
-- Dependencies: 294
-- Name: synonyms_syn_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.synonyms_syn_id_seq', 1, false);


--
-- TOC entry 3826 (class 0 OID 0)
-- Dependencies: 296
-- Name: target_class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.target_class_id_seq', 39, true);


--
-- TOC entry 3827 (class 0 OID 0)
-- Dependencies: 298
-- Name: target_component_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.target_component_id_seq', 1, false);


--
-- TOC entry 3828 (class 0 OID 0)
-- Dependencies: 300
-- Name: target_dictionary_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.target_dictionary_id_seq', 1, false);


--
-- TOC entry 3829 (class 0 OID 0)
-- Dependencies: 305
-- Name: tdgo2tc_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.tdgo2tc_id_seq', 1, false);


--
-- TOC entry 3830 (class 0 OID 0)
-- Dependencies: 307
-- Name: tdkey2tc_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jjyang
--

SELECT pg_catalog.setval('public.tdkey2tc_id_seq', 1, false);


--
-- TOC entry 3361 (class 2606 OID 562694)
-- Name: structures cas_reg_no_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT cas_reg_no_uq UNIQUE (cas_reg_no);


--
-- TOC entry 3234 (class 2606 OID 562696)
-- Name: ddi_risk ddi_risk_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi_risk
    ADD CONSTRAINT ddi_risk_uq UNIQUE (risk, ddi_ref_id);


--
-- TOC entry 3230 (class 2606 OID 562698)
-- Name: ddi ddi_tuple_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_tuple_uq UNIQUE (drug_class1, drug_class2, ddi_ref_id);


--
-- TOC entry 3257 (class 2606 OID 562700)
-- Name: identifier identifier_unique; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_unique UNIQUE (identifier, id_type, struct_id);


--
-- TOC entry 3287 (class 2606 OID 562702)
-- Name: omop_relationship omoprel_struct_concept_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT omoprel_struct_concept_uq UNIQUE (struct_id, concept_id);


--
-- TOC entry 3308 (class 2606 OID 562704)
-- Name: prd2label prd2label_id_key; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd2label_id_key UNIQUE (id);


--
-- TOC entry 3312 (class 2606 OID 562706)
-- Name: product prd_ndc_uniq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT prd_ndc_uniq UNIQUE (ndc_product_code);


--
-- TOC entry 3363 (class 2606 OID 562708)
-- Name: structures sql100501171817150; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT sql100501171817150 PRIMARY KEY (cd_id);


--
-- TOC entry 3226 (class 2606 OID 562710)
-- Name: data_source sql100517171435170; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.data_source
    ADD CONSTRAINT sql100517171435170 PRIMARY KEY (src_id);


--
-- TOC entry 3265 (class 2606 OID 562712)
-- Name: label sql120404123647220; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.label
    ADD CONSTRAINT sql120404123647220 PRIMARY KEY (id);


--
-- TOC entry 3337 (class 2606 OID 562714)
-- Name: section sql120404123658530; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.section
    ADD CONSTRAINT sql120404123658530 PRIMARY KEY (id);


--
-- TOC entry 3204 (class 2606 OID 562716)
-- Name: active_ingredient sql120404123859150; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT sql120404123859150 PRIMARY KEY (id);


--
-- TOC entry 3314 (class 2606 OID 562718)
-- Name: product sql120412001426700; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT sql120412001426700 PRIMARY KEY (id);


--
-- TOC entry 3340 (class 2606 OID 562720)
-- Name: struct2atc sql120523120921130; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT sql120523120921130 PRIMARY KEY (struct_id, atc_code);


--
-- TOC entry 3355 (class 2606 OID 562722)
-- Name: structure_type sql120925163230900; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT sql120925163230900 PRIMARY KEY (id);


--
-- TOC entry 3289 (class 2606 OID 562724)
-- Name: omop_relationship sql121023161238820; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT sql121023161238820 PRIMARY KEY (id);


--
-- TOC entry 3216 (class 2606 OID 562726)
-- Name: atc sql130424125636180; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc
    ADD CONSTRAINT sql130424125636180 PRIMARY KEY (id);


--
-- TOC entry 3218 (class 2606 OID 562728)
-- Name: atc sql130424125636181; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc
    ADD CONSTRAINT sql130424125636181 UNIQUE (code);


--
-- TOC entry 3379 (class 2606 OID 562730)
-- Name: target_class sql130710170452610; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_class
    ADD CONSTRAINT sql130710170452610 PRIMARY KEY (l1);


--
-- TOC entry 3310 (class 2606 OID 562732)
-- Name: prd2label sql130919144750040; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT sql130919144750040 PRIMARY KEY (ndc_product_code, label_id);


--
-- TOC entry 3261 (class 2606 OID 562734)
-- Name: inn_stem sql140212001438200; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.inn_stem
    ADD CONSTRAINT sql140212001438200 PRIMARY KEY (id);


--
-- TOC entry 3263 (class 2606 OID 562736)
-- Name: inn_stem sql140212001438201; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.inn_stem
    ADD CONSTRAINT sql140212001438201 UNIQUE (stem);


--
-- TOC entry 3323 (class 2606 OID 562738)
-- Name: ref_type sql140401160903570; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ref_type
    ADD CONSTRAINT sql140401160903570 PRIMARY KEY (id);


--
-- TOC entry 3325 (class 2606 OID 562740)
-- Name: ref_type sql140401160903571; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ref_type
    ADD CONSTRAINT sql140401160903571 UNIQUE (type);


--
-- TOC entry 3247 (class 2606 OID 562742)
-- Name: drug_class sql140409195222370; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.drug_class
    ADD CONSTRAINT sql140409195222370 PRIMARY KEY (id);


--
-- TOC entry 3249 (class 2606 OID 562744)
-- Name: drug_class sql140409195222371; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.drug_class
    ADD CONSTRAINT sql140409195222371 UNIQUE (name);


--
-- TOC entry 3222 (class 2606 OID 562746)
-- Name: attr_type sql140410123913680; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.attr_type
    ADD CONSTRAINT sql140410123913680 PRIMARY KEY (id);


--
-- TOC entry 3224 (class 2606 OID 562748)
-- Name: attr_type sql140410123913681; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.attr_type
    ADD CONSTRAINT sql140410123913681 UNIQUE (name);


--
-- TOC entry 3236 (class 2606 OID 562750)
-- Name: ddi_risk sql140411131027290; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi_risk
    ADD CONSTRAINT sql140411131027290 PRIMARY KEY (id);


--
-- TOC entry 3232 (class 2606 OID 562752)
-- Name: ddi sql140411131553960; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT sql140411131553960 PRIMARY KEY (id);


--
-- TOC entry 3220 (class 2606 OID 562754)
-- Name: atc_ddd sql140512172435200; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc_ddd
    ADD CONSTRAINT sql140512172435200 PRIMARY KEY (id);


--
-- TOC entry 3253 (class 2606 OID 562756)
-- Name: id_type sql140607012055120; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.id_type
    ADD CONSTRAINT sql140607012055120 PRIMARY KEY (id);


--
-- TOC entry 3255 (class 2606 OID 562758)
-- Name: id_type sql140607012055121; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.id_type
    ADD CONSTRAINT sql140607012055121 UNIQUE (type);


--
-- TOC entry 3259 (class 2606 OID 562760)
-- Name: identifier sql140607225949710; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT sql140607225949710 PRIMARY KEY (id);


--
-- TOC entry 3345 (class 2606 OID 562762)
-- Name: struct2drgclass sql140608153701270; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT sql140608153701270 PRIMARY KEY (id);


--
-- TOC entry 3347 (class 2606 OID 562764)
-- Name: struct2drgclass sql140608153701271; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT sql140608153701271 UNIQUE (struct_id, drug_class_id);


--
-- TOC entry 3351 (class 2606 OID 562766)
-- Name: struct_type_def sql141016095933600; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct_type_def
    ADD CONSTRAINT sql141016095933600 PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 562768)
-- Name: struct_type_def sql141016095933601; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct_type_def
    ADD CONSTRAINT sql141016095933601 UNIQUE (type);


--
-- TOC entry 3212 (class 2606 OID 562770)
-- Name: approval_type sql141031231522060; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval_type
    ADD CONSTRAINT sql141031231522060 PRIMARY KEY (id);


--
-- TOC entry 3214 (class 2606 OID 562772)
-- Name: approval_type sql141031231522061; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval_type
    ADD CONSTRAINT sql141031231522061 UNIQUE (descr);


--
-- TOC entry 3208 (class 2606 OID 562774)
-- Name: approval sql141031231617260; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT sql141031231617260 PRIMARY KEY (id);


--
-- TOC entry 3210 (class 2606 OID 562776)
-- Name: approval sql141031231617263; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT sql141031231617263 UNIQUE (struct_id, type);


--
-- TOC entry 3327 (class 2606 OID 562778)
-- Name: reference sql141129210458570; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458570 PRIMARY KEY (id);


--
-- TOC entry 3329 (class 2606 OID 562780)
-- Name: reference sql141129210458571; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458571 UNIQUE (pmid);


--
-- TOC entry 3331 (class 2606 OID 562782)
-- Name: reference sql141129210458572; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458572 UNIQUE (doi);


--
-- TOC entry 3333 (class 2606 OID 562784)
-- Name: reference sql141129210458573; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458573 UNIQUE (document_id);


--
-- TOC entry 3335 (class 2606 OID 562786)
-- Name: reference sql141129210458574; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT sql141129210458574 UNIQUE (isbn10);


--
-- TOC entry 3381 (class 2606 OID 562788)
-- Name: target_component sql141203005155660; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155660 PRIMARY KEY (id);


--
-- TOC entry 3383 (class 2606 OID 562790)
-- Name: target_component sql141203005155670; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155670 UNIQUE (accession);


--
-- TOC entry 3385 (class 2606 OID 562792)
-- Name: target_component sql141203005155671; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_component
    ADD CONSTRAINT sql141203005155671 UNIQUE (swissprot);


--
-- TOC entry 3319 (class 2606 OID 562794)
-- Name: protein_type sql141203213631730; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.protein_type
    ADD CONSTRAINT sql141203213631730 PRIMARY KEY (id);


--
-- TOC entry 3321 (class 2606 OID 562796)
-- Name: protein_type sql141203213631731; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.protein_type
    ADD CONSTRAINT sql141203213631731 UNIQUE (type);


--
-- TOC entry 3387 (class 2606 OID 562798)
-- Name: target_dictionary sql141205191111190; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_dictionary
    ADD CONSTRAINT sql141205191111190 PRIMARY KEY (id);


--
-- TOC entry 3393 (class 2606 OID 562800)
-- Name: td2tc sql141205191436250; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.td2tc
    ADD CONSTRAINT sql141205191436250 PRIMARY KEY (target_id, component_id);


--
-- TOC entry 3200 (class 2606 OID 562802)
-- Name: action_type sql141210123613760; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.action_type
    ADD CONSTRAINT sql141210123613760 PRIMARY KEY (id);


--
-- TOC entry 3202 (class 2606 OID 562804)
-- Name: action_type sql141210123613761; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.action_type
    ADD CONSTRAINT sql141210123613761 UNIQUE (action_type);


--
-- TOC entry 3391 (class 2606 OID 562806)
-- Name: target_keyword sql141211160454700; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_keyword
    ADD CONSTRAINT sql141211160454700 PRIMARY KEY (id);


--
-- TOC entry 3397 (class 2606 OID 562808)
-- Name: tdkey2tc sql141211195643960; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT sql141211195643960 PRIMARY KEY (id);


--
-- TOC entry 3389 (class 2606 OID 562810)
-- Name: target_go sql141211234759820; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.target_go
    ADD CONSTRAINT sql141211234759820 PRIMARY KEY (id);


--
-- TOC entry 3395 (class 2606 OID 562812)
-- Name: tdgo2tc sql141211235052890; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT sql141211235052890 PRIMARY KEY (id);


--
-- TOC entry 3298 (class 2606 OID 562814)
-- Name: pdb sql150123095054720; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT sql150123095054720 PRIMARY KEY (id);


--
-- TOC entry 3300 (class 2606 OID 562816)
-- Name: pdb sql150123095054722; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT sql150123095054722 UNIQUE (struct_id, pdb);


--
-- TOC entry 3238 (class 2606 OID 562818)
-- Name: doid sql150425232401220; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid
    ADD CONSTRAINT sql150425232401220 PRIMARY KEY (id);


--
-- TOC entry 3240 (class 2606 OID 562820)
-- Name: doid sql150425232401221; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid
    ADD CONSTRAINT sql150425232401221 UNIQUE (doid);


--
-- TOC entry 3242 (class 2606 OID 562822)
-- Name: doid_xref sql150426005334630; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT sql150426005334630 PRIMARY KEY (id);


--
-- TOC entry 3244 (class 2606 OID 562824)
-- Name: doid_xref sql150426005334632; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT sql150426005334632 UNIQUE (doid, source, xref);


--
-- TOC entry 3292 (class 2606 OID 562826)
-- Name: parentmol sql150523184351770; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184351770 PRIMARY KEY (cd_id);


--
-- TOC entry 3294 (class 2606 OID 562828)
-- Name: parentmol sql150523184621160; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184621160 UNIQUE (name);


--
-- TOC entry 3296 (class 2606 OID 562830)
-- Name: parentmol sql150523184644290; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.parentmol
    ADD CONSTRAINT sql150523184644290 UNIQUE (cas_reg_no);


--
-- TOC entry 3349 (class 2606 OID 562832)
-- Name: struct2parent sql150529131801300; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT sql150529131801300 PRIMARY KEY (struct_id, parent_id);


--
-- TOC entry 3302 (class 2606 OID 562834)
-- Name: pharma_class sql150603161251830; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT sql150603161251830 PRIMARY KEY (id);


--
-- TOC entry 3304 (class 2606 OID 562836)
-- Name: pharma_class sql150603161251841; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT sql150603161251841 UNIQUE (struct_id, type, name);


--
-- TOC entry 3369 (class 2606 OID 562838)
-- Name: synonyms sql150826201920370; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920370 PRIMARY KEY (syn_id);


--
-- TOC entry 3371 (class 2606 OID 562840)
-- Name: synonyms sql150826201920371; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920371 UNIQUE (name);


--
-- TOC entry 3373 (class 2606 OID 562842)
-- Name: synonyms sql150826201920374; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920374 UNIQUE (id, preferred_name);


--
-- TOC entry 3375 (class 2606 OID 562844)
-- Name: synonyms sql150826201920375; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT sql150826201920375 UNIQUE (parent_id, preferred_name);


--
-- TOC entry 3198 (class 2606 OID 562846)
-- Name: act_table_full sql160219095125231; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT sql160219095125231 PRIMARY KEY (act_id);


--
-- TOC entry 3228 (class 2606 OID 562848)
-- Name: dbversion sql160415165555160; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.dbversion
    ADD CONSTRAINT sql160415165555160 PRIMARY KEY (version);


--
-- TOC entry 3277 (class 2606 OID 562850)
-- Name: ob_product sql161126154442460; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_product
    ADD CONSTRAINT sql161126154442460 PRIMARY KEY (id);


--
-- TOC entry 3279 (class 2606 OID 562853)
-- Name: struct2obprod sql161126154450310; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT sql161126154450310 PRIMARY KEY (struct_id, prod_id);


--
-- TOC entry 3271 (class 2606 OID 562855)
-- Name: ob_exclusivity sql161127115514940; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_exclusivity
    ADD CONSTRAINT sql161127115514940 PRIMARY KEY (id);


--
-- TOC entry 3283 (class 2606 OID 562860)
-- Name: ob_patent sql161127120038110; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_patent
    ADD CONSTRAINT sql161127120038110 PRIMARY KEY (id);


--
-- TOC entry 3273 (class 2606 OID 562862)
-- Name: ob_exclusivity_code sql161127133109120; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_exclusivity_code
    ADD CONSTRAINT sql161127133109120 PRIMARY KEY (code);


--
-- TOC entry 3285 (class 2606 OID 562864)
-- Name: ob_patent_use_code sql161127133626130; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_patent_use_code
    ADD CONSTRAINT sql161127133626130 PRIMARY KEY (code);


--
-- TOC entry 3306 (class 2606 OID 562866)
-- Name: pka sql180408153602180; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pka
    ADD CONSTRAINT sql180408153602180 PRIMARY KEY (id);


--
-- TOC entry 3251 (class 2606 OID 562868)
-- Name: faers sql180422234202640; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers
    ADD CONSTRAINT sql180422234202640 PRIMARY KEY (id);


--
-- TOC entry 3267 (class 2606 OID 562870)
-- Name: lincs_signature sql180509154922470; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.lincs_signature
    ADD CONSTRAINT sql180509154922470 PRIMARY KEY (id);


--
-- TOC entry 3269 (class 2606 OID 562887)
-- Name: lincs_signature sql180509154922471; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.lincs_signature
    ADD CONSTRAINT sql180509154922471 UNIQUE (struct_id1, struct_id2, is_parent1, is_parent2, cell_id);


--
-- TOC entry 3343 (class 2606 OID 562889)
-- Name: struct2atc struct2atc_id_key; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_id_key UNIQUE (id);


--
-- TOC entry 3359 (class 2606 OID 562891)
-- Name: structure_type struct_id_type_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT struct_id_type_uq UNIQUE (struct_id, type);


--
-- TOC entry 3317 (class 2606 OID 562893)
-- Name: property_type symbol; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.property_type
    ADD CONSTRAINT symbol PRIMARY KEY (id);


--
-- TOC entry 3377 (class 2606 OID 562895)
-- Name: synonyms syn_lname_uq; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT syn_lname_uq UNIQUE (lname);


--
-- TOC entry 3367 (class 2606 OID 562897)
-- Name: structures uniq_structures_id; Type: CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT uniq_structures_id UNIQUE (id);


--
-- TOC entry 3280 (class 1259 OID 562898)
-- Name: ob_patent_applno_idx; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX ob_patent_applno_idx ON public.ob_patent USING btree (appl_no);


--
-- TOC entry 3281 (class 1259 OID 562899)
-- Name: ob_patent_prodno_idx; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX ob_patent_prodno_idx ON public.ob_patent USING btree (product_no);


--
-- TOC entry 3274 (class 1259 OID 562900)
-- Name: ob_product_appno_idx; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX ob_product_appno_idx ON public.ob_product USING btree (appl_no);


--
-- TOC entry 3275 (class 1259 OID 562901)
-- Name: ob_product_prodno_idx; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX ob_product_prodno_idx ON public.ob_product USING btree (product_no);


--
-- TOC entry 3364 (class 1259 OID 562902)
-- Name: sql100501183943930; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE UNIQUE INDEX sql100501183943930 ON public.structures USING btree (id);


--
-- TOC entry 3338 (class 1259 OID 562903)
-- Name: sql120404123658531; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120404123658531 ON public.section USING btree (label_id);


--
-- TOC entry 3205 (class 1259 OID 562904)
-- Name: sql120404123859152; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120404123859152 ON public.active_ingredient USING btree (struct_id);


--
-- TOC entry 3315 (class 1259 OID 562905)
-- Name: sql120412004620930; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE UNIQUE INDEX sql120412004620930 ON public.product USING btree (ndc_product_code);


--
-- TOC entry 3206 (class 1259 OID 562906)
-- Name: sql120412004634310; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120412004634310 ON public.active_ingredient USING btree (ndc_product_code);


--
-- TOC entry 3365 (class 1259 OID 562907)
-- Name: sql120418113711390; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120418113711390 ON public.structures USING btree (cas_reg_no);


--
-- TOC entry 3341 (class 1259 OID 562908)
-- Name: sql120523120921131; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120523120921131 ON public.struct2atc USING btree (struct_id);


--
-- TOC entry 3356 (class 1259 OID 562909)
-- Name: sql120925163230920; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120925163230920 ON public.structure_type USING btree (struct_id);


--
-- TOC entry 3357 (class 1259 OID 562910)
-- Name: sql120925163230921; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql120925163230921 ON public.structure_type USING btree (struct_id, type);


--
-- TOC entry 3290 (class 1259 OID 562911)
-- Name: sql121023161238850; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX sql121023161238850 ON public.omop_relationship USING btree (struct_id);


--
-- TOC entry 3245 (class 1259 OID 562912)
-- Name: xref_source_idx; Type: INDEX; Schema: public; Owner: jjyang
--

CREATE INDEX xref_source_idx ON public.doid_xref USING btree (xref, source);


--
-- TOC entry 3398 (class 2606 OID 562913)
-- Name: act_table_full act_table_full_2_act_type; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_act_type FOREIGN KEY (action_type) REFERENCES public.action_type(action_type);


--
-- TOC entry 3399 (class 2606 OID 562918)
-- Name: act_table_full act_table_full_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3400 (class 2606 OID 562923)
-- Name: act_table_full act_table_full_2_target_class; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_target_class FOREIGN KEY (target_class) REFERENCES public.target_class(l1);


--
-- TOC entry 3401 (class 2606 OID 562928)
-- Name: act_table_full act_table_full_2_target_dict; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT act_table_full_2_target_dict FOREIGN KEY (target_id) REFERENCES public.target_dictionary(id);


--
-- TOC entry 3404 (class 2606 OID 562933)
-- Name: active_ingredient active_ingredient_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT active_ingredient_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3405 (class 2606 OID 562938)
-- Name: active_ingredient active_ingredient_ndc_product_code_fkey; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.active_ingredient
    ADD CONSTRAINT active_ingredient_ndc_product_code_fkey FOREIGN KEY (ndc_product_code) REFERENCES public.product(ndc_product_code);


--
-- TOC entry 3406 (class 2606 OID 562943)
-- Name: approval approval_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT approval_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3407 (class 2606 OID 562948)
-- Name: approval approval_2_type; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.approval
    ADD CONSTRAINT approval_2_type FOREIGN KEY (type) REFERENCES public.approval_type(descr);


--
-- TOC entry 3408 (class 2606 OID 562953)
-- Name: atc_ddd atc_ddd_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.atc_ddd
    ADD CONSTRAINT atc_ddd_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3402 (class 2606 OID 562958)
-- Name: act_table_full bioact_2_ref; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT bioact_2_ref FOREIGN KEY (act_ref_id) REFERENCES public.reference(id);


--
-- TOC entry 3409 (class 2606 OID 562963)
-- Name: ddi ddi_2_ddi_risk; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_ddi_risk FOREIGN KEY (ddi_risk, ddi_ref_id) REFERENCES public.ddi_risk(risk, ddi_ref_id);


--
-- TOC entry 3410 (class 2606 OID 562968)
-- Name: ddi ddi_2_drug_class1; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_drug_class1 FOREIGN KEY (drug_class1) REFERENCES public.drug_class(name);


--
-- TOC entry 3411 (class 2606 OID 562973)
-- Name: ddi ddi_2_drug_class2; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ddi
    ADD CONSTRAINT ddi_2_drug_class2 FOREIGN KEY (drug_class2) REFERENCES public.drug_class(name);


--
-- TOC entry 3412 (class 2606 OID 562978)
-- Name: doid_xref doid_xref_2_doid; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.doid_xref
    ADD CONSTRAINT doid_xref_2_doid FOREIGN KEY (doid) REFERENCES public.doid(doid);


--
-- TOC entry 3413 (class 2606 OID 562983)
-- Name: faers faers_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers
    ADD CONSTRAINT faers_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3414 (class 2606 OID 562988)
-- Name: faers_female faers_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers_female
    ADD CONSTRAINT faers_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3415 (class 2606 OID 562993)
-- Name: faers_male faers_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.faers_male
    ADD CONSTRAINT faers_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3416 (class 2606 OID 562998)
-- Name: identifier identifier_2_idtype; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_2_idtype FOREIGN KEY (id_type) REFERENCES public.id_type(type);


--
-- TOC entry 3417 (class 2606 OID 563003)
-- Name: identifier identifier_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.identifier
    ADD CONSTRAINT identifier_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3403 (class 2606 OID 563008)
-- Name: act_table_full moa_2_ref; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.act_table_full
    ADD CONSTRAINT moa_2_ref FOREIGN KEY (moa_ref_id) REFERENCES public.reference(id);


--
-- TOC entry 3418 (class 2606 OID 563013)
-- Name: ob_exclusivity obexcl_2_exclcode; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_exclusivity
    ADD CONSTRAINT obexcl_2_exclcode FOREIGN KEY (exclusivity_code) REFERENCES public.ob_exclusivity_code(code);


--
-- TOC entry 3421 (class 2606 OID 563018)
-- Name: ob_patent obpat_2_usecode; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.ob_patent
    ADD CONSTRAINT obpat_2_usecode FOREIGN KEY (patent_use_code) REFERENCES public.ob_patent_use_code(code);


--
-- TOC entry 3419 (class 2606 OID 563023)
-- Name: struct2obprod obprod_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT obprod_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3422 (class 2606 OID 563028)
-- Name: omop_relationship omop_relationship_struct_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.omop_relationship
    ADD CONSTRAINT omop_relationship_struct_id_fkey FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3423 (class 2606 OID 563033)
-- Name: pdb pdb_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pdb
    ADD CONSTRAINT pdb_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3424 (class 2606 OID 563038)
-- Name: pharma_class pharma_class_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pharma_class
    ADD CONSTRAINT pharma_class_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3425 (class 2606 OID 563043)
-- Name: pka pka_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.pka
    ADD CONSTRAINT pka_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3426 (class 2606 OID 563048)
-- Name: prd2label prd_2_label; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd_2_label FOREIGN KEY (label_id) REFERENCES public.label(id);


--
-- TOC entry 3427 (class 2606 OID 563053)
-- Name: prd2label prd_2_prd; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.prd2label
    ADD CONSTRAINT prd_2_prd FOREIGN KEY (ndc_product_code) REFERENCES public.product(ndc_product_code);


--
-- TOC entry 3428 (class 2606 OID 563058)
-- Name: reference reference_2_reftype; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT reference_2_reftype FOREIGN KEY (type) REFERENCES public.ref_type(type);


--
-- TOC entry 3429 (class 2606 OID 563063)
-- Name: section section_2_label; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.section
    ADD CONSTRAINT section_2_label FOREIGN KEY (label_id) REFERENCES public.label(id);


--
-- TOC entry 3430 (class 2606 OID 563068)
-- Name: struct2atc struct2atc_2_atc; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_2_atc FOREIGN KEY (atc_code) REFERENCES public.atc(code);


--
-- TOC entry 3431 (class 2606 OID 563073)
-- Name: struct2atc struct2atc_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2atc
    ADD CONSTRAINT struct2atc_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3432 (class 2606 OID 563078)
-- Name: struct2drgclass struct2drgclass_2_drgclass; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT struct2drgclass_2_drgclass FOREIGN KEY (drug_class_id) REFERENCES public.drug_class(id);


--
-- TOC entry 3433 (class 2606 OID 563083)
-- Name: struct2drgclass struct2drgclass_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2drgclass
    ADD CONSTRAINT struct2drgclass_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3434 (class 2606 OID 563088)
-- Name: struct2parent struct2parent_2_parent; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT struct2parent_2_parent FOREIGN KEY (parent_id) REFERENCES public.parentmol(cd_id);


--
-- TOC entry 3435 (class 2606 OID 563093)
-- Name: struct2parent struct2parent_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2parent
    ADD CONSTRAINT struct2parent_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3420 (class 2606 OID 563098)
-- Name: struct2obprod struct_2_obprod; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.struct2obprod
    ADD CONSTRAINT struct_2_obprod FOREIGN KEY (prod_id) REFERENCES public.ob_product(id);


--
-- TOC entry 3438 (class 2606 OID 563103)
-- Name: structures struct_2_stem; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structures
    ADD CONSTRAINT struct_2_stem FOREIGN KEY (stem) REFERENCES public.inn_stem(stem);


--
-- TOC entry 3436 (class 2606 OID 563108)
-- Name: structure_type structure_type_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT structure_type_2_struct FOREIGN KEY (struct_id) REFERENCES public.structures(id);


--
-- TOC entry 3437 (class 2606 OID 563113)
-- Name: structure_type structure_type_2_type; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.structure_type
    ADD CONSTRAINT structure_type_2_type FOREIGN KEY (type) REFERENCES public.struct_type_def(type);


--
-- TOC entry 3439 (class 2606 OID 563118)
-- Name: synonyms synonym_2_parent; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT synonym_2_parent FOREIGN KEY (parent_id) REFERENCES public.parentmol(cd_id);


--
-- TOC entry 3440 (class 2606 OID 563123)
-- Name: synonyms synonym_2_struct; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.synonyms
    ADD CONSTRAINT synonym_2_struct FOREIGN KEY (id) REFERENCES public.structures(id);


--
-- TOC entry 3441 (class 2606 OID 563128)
-- Name: td2tc td2tc_2_td; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.td2tc
    ADD CONSTRAINT td2tc_2_td FOREIGN KEY (target_id) REFERENCES public.target_dictionary(id);


--
-- TOC entry 3442 (class 2606 OID 563133)
-- Name: tdgo2tc tdgo2tc_2_go; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT tdgo2tc_2_go FOREIGN KEY (go_id) REFERENCES public.target_go(id);


--
-- TOC entry 3444 (class 2606 OID 563138)
-- Name: tdkey2tc tdgo2tc_2_kw; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT tdgo2tc_2_kw FOREIGN KEY (tdkey_id) REFERENCES public.target_keyword(id);


--
-- TOC entry 3443 (class 2606 OID 563143)
-- Name: tdgo2tc tdgo2tc_2_tc; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdgo2tc
    ADD CONSTRAINT tdgo2tc_2_tc FOREIGN KEY (component_id) REFERENCES public.target_component(id);


--
-- TOC entry 3445 (class 2606 OID 563148)
-- Name: tdkey2tc tdgo2tc_2_tc; Type: FK CONSTRAINT; Schema: public; Owner: jjyang
--

ALTER TABLE ONLY public.tdkey2tc
    ADD CONSTRAINT tdgo2tc_2_tc FOREIGN KEY (component_id) REFERENCES public.target_component(id);


-- Completed on 2020-09-18 10:51:04 MDT

--
-- PostgreSQL database dump complete
--


