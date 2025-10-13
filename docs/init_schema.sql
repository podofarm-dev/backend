--
-- PostgreSQL database dump
--

\restrict 5Nw8lcz4JvDv7Zz5u3dQ0AHvutTn7wwRVMMDp4BSuhMLa35L4HObjJKPFJsPLgd

-- Dumped from database version 14.19 (Homebrew)
-- Dumped by pg_dump version 14.19 (Homebrew)

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
-- Name: code; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code (
    code_no bigint NOT NULL,
    code_source character varying(255),
    code_solved_date timestamp without time zone,
    code_annotation character varying(255),
    code_status boolean,
    code_time time without time zone,
    code_performance character varying(255),
    code_accuracy character varying(255),
    member_id character varying(255),
    problem_id bigint
);


--
-- Name: code_code_no_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.code_code_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: code_code_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.code_code_no_seq OWNED BY public.code.code_no;


--
-- Name: comment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.comment (
    comment_no bigint NOT NULL,
    comment_content character varying(255),
    comment_date timestamp without time zone,
    member_id character varying(255),
    code_no bigint
);


--
-- Name: comment_comment_no_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.comment_comment_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: comment_comment_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.comment_comment_no_seq OWNED BY public.comment.comment_no;


--
-- Name: member; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.member (
    member_id character varying(255) NOT NULL,
    member_name character varying(255),
    member_googleid character varying(255),
    member_email character varying(255),
    member_solvedproblem integer DEFAULT 0,
    member_leader character varying(255) DEFAULT 'N'::character varying,
    member_isparticipant date,
    member_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    member_img_url character varying(255),
    study_id character varying(255)
);


--
-- Name: problem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.problem (
    problem_id bigint NOT NULL,
    problem_no bigint,
    problem_title character varying(255),
    problem_level character varying(255),
    problem_link character varying(255),
    problem_readme character varying(255),
    problem_type character varying(255),
    problem_solution character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


--
-- Name: study; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.study (
    study_id character varying(255) NOT NULL,
    study_name character varying(255),
    study_pwd character varying(255),
    study_start date,
    study_end date
);


--
-- Name: token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.token (
    token_no bigint NOT NULL,
    refresh_token character varying(255),
    access_token character varying(255),
    refresh_expiration_time timestamp without time zone,
    member_id character varying(255)
);


--
-- Name: token_token_no_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.token_token_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: token_token_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.token_token_no_seq OWNED BY public.token.token_no;


--
-- Name: code code_no; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code ALTER COLUMN code_no SET DEFAULT nextval('public.code_code_no_seq'::regclass);


--
-- Name: comment comment_no; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comment ALTER COLUMN comment_no SET DEFAULT nextval('public.comment_comment_no_seq'::regclass);


--
-- Name: token token_no; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.token ALTER COLUMN token_no SET DEFAULT nextval('public.token_token_no_seq'::regclass);


--
-- Name: code code_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code
    ADD CONSTRAINT code_pkey PRIMARY KEY (code_no);


--
-- Name: comment comment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (comment_no);


--
-- Name: member member_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member
    ADD CONSTRAINT member_pkey PRIMARY KEY (member_id);


--
-- Name: problem problem_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.problem
    ADD CONSTRAINT problem_pkey PRIMARY KEY (problem_id);


--
-- Name: study study_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study
    ADD CONSTRAINT study_pkey PRIMARY KEY (study_id);


--
-- Name: token token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (token_no);


--
-- Name: idx_code_member_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_code_member_id ON public.code USING btree (member_id);


--
-- Name: idx_code_problem_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_code_problem_id ON public.code USING btree (problem_id);


--
-- Name: idx_code_solved_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_code_solved_date ON public.code USING btree (code_solved_date);


--
-- Name: idx_comment_code_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_comment_code_no ON public.comment USING btree (code_no);


--
-- Name: idx_comment_member_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_comment_member_id ON public.comment USING btree (member_id);


--
-- Name: idx_member_email; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_member_email ON public.member USING btree (member_email);


--
-- Name: idx_member_study_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_member_study_id ON public.member USING btree (study_id);


--
-- Name: idx_problem_level; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_problem_level ON public.problem USING btree (problem_level);


--
-- Name: idx_token_member_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_token_member_id ON public.token USING btree (member_id);


--
-- Name: code fk_code_member; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code
    ADD CONSTRAINT fk_code_member FOREIGN KEY (member_id) REFERENCES public.member(member_id) ON DELETE CASCADE;


--
-- Name: code fk_code_problem; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code
    ADD CONSTRAINT fk_code_problem FOREIGN KEY (problem_id) REFERENCES public.problem(problem_id) ON DELETE CASCADE;


--
-- Name: comment fk_comment_code; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comment
    ADD CONSTRAINT fk_comment_code FOREIGN KEY (code_no) REFERENCES public.code(code_no) ON DELETE CASCADE;


--
-- Name: comment fk_comment_member; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comment
    ADD CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES public.member(member_id) ON DELETE CASCADE;


--
-- Name: member fk_member_study; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member
    ADD CONSTRAINT fk_member_study FOREIGN KEY (study_id) REFERENCES public.study(study_id) ON DELETE SET NULL;


--
-- Name: token fk_token_member; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT fk_token_member FOREIGN KEY (member_id) REFERENCES public.member(member_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict 5Nw8lcz4JvDv7Zz5u3dQ0AHvutTn7wwRVMMDp4BSuhMLa35L4HObjJKPFJsPLgd

