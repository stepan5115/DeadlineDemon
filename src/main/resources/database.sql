--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.4

-- Started on 2025-04-25 23:32:56

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

--
-- TOC entry 4909 (class 1262 OID 25776)
-- Name: homework_bot; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE homework_bot WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';


ALTER DATABASE homework_bot OWNER TO postgres;

\connect homework_bot

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
-- TOC entry 224 (class 1259 OID 25836)
-- Name: admin_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin_tokens (
                                     id integer NOT NULL,
                                     token character varying(100) NOT NULL,
                                     created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     expires_at timestamp without time zone NOT NULL,
                                     created_by integer
);


ALTER TABLE public.admin_tokens OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 25835)
-- Name: admin_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admin_tokens_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admin_tokens_id_seq OWNER TO postgres;

--
-- TOC entry 4910 (class 0 OID 0)
-- Dependencies: 223
-- Name: admin_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admin_tokens_id_seq OWNED BY public.admin_tokens.id;


--
-- TOC entry 222 (class 1259 OID 25810)
-- Name: assignments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.assignments (
                                    assignment_id integer NOT NULL,
                                    title character varying(100) NOT NULL,
                                    description text,
                                    groups jsonb DEFAULT '[]'::jsonb,
                                    deadline timestamp without time zone NOT NULL,
                                    created_at timestamp without time zone DEFAULT now(),
                                    subject_id integer
);


ALTER TABLE public.assignments OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 25809)
-- Name: assignments_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.assignments_assignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.assignments_assignment_id_seq OWNER TO postgres;

--
-- TOC entry 4911 (class 0 OID 0)
-- Dependencies: 221
-- Name: assignments_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.assignments_assignment_id_seq OWNED BY public.assignments.assignment_id;


--
-- TOC entry 218 (class 1259 OID 25787)
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
                               group_id integer NOT NULL,
                               name character varying(100) NOT NULL
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 25786)
-- Name: groups_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.groups_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.groups_group_id_seq OWNER TO postgres;

--
-- TOC entry 4912 (class 0 OID 0)
-- Dependencies: 217
-- Name: groups_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.groups_group_id_seq OWNED BY public.groups.group_id;


--
-- TOC entry 226 (class 1259 OID 25896)
-- Name: notifications_sent; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notifications_sent (
                                           id integer NOT NULL,
                                           assignment_id bigint,
                                           chat_id bigint,
                                           sent_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.notifications_sent OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 25895)
-- Name: notifications_sent_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.notifications_sent_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notifications_sent_id_seq OWNER TO postgres;

--
-- TOC entry 4913 (class 0 OID 0)
-- Dependencies: 225
-- Name: notifications_sent_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.notifications_sent_id_seq OWNED BY public.notifications_sent.id;


--
-- TOC entry 228 (class 1259 OID 26102)
-- Name: pending_notifications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pending_notifications (
                                              id bigint NOT NULL,
                                              chat_id bigint NOT NULL
);


ALTER TABLE public.pending_notifications OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 26101)
-- Name: pending_notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pending_notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pending_notifications_id_seq OWNER TO postgres;

--
-- TOC entry 4914 (class 0 OID 0)
-- Dependencies: 227
-- Name: pending_notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pending_notifications_id_seq OWNED BY public.pending_notifications.id;


--
-- TOC entry 216 (class 1259 OID 25778)
-- Name: subjects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subjects (
                                 subject_id integer NOT NULL,
                                 name character varying(100) NOT NULL
);


ALTER TABLE public.subjects OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 25777)
-- Name: subjects_subject_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.subjects_subject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.subjects_subject_id_seq OWNER TO postgres;

--
-- TOC entry 4915 (class 0 OID 0)
-- Dependencies: 215
-- Name: subjects_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.subjects_subject_id_seq OWNED BY public.subjects.subject_id;


--
-- TOC entry 220 (class 1259 OID 25796)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
                              user_id integer NOT NULL,
                              username character varying(50) NOT NULL,
                              password character varying(100) NOT NULL,
                              groups jsonb DEFAULT '[]'::jsonb,
                              can_edit_assignments boolean DEFAULT false,
                              allow_notifications boolean DEFAULT false,
                              notification_interval bigint,
                              completed_assignments jsonb DEFAULT '[]'::jsonb,
                              notification_excluded_subjects jsonb DEFAULT '[]'::jsonb
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 25795)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 4916 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4729 (class 2604 OID 25839)
-- Name: admin_tokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_tokens ALTER COLUMN id SET DEFAULT nextval('public.admin_tokens_id_seq'::regclass);


--
-- TOC entry 4726 (class 2604 OID 25813)
-- Name: assignments assignment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments ALTER COLUMN assignment_id SET DEFAULT nextval('public.assignments_assignment_id_seq'::regclass);


--
-- TOC entry 4719 (class 2604 OID 25790)
-- Name: groups group_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups ALTER COLUMN group_id SET DEFAULT nextval('public.groups_group_id_seq'::regclass);


--
-- TOC entry 4731 (class 2604 OID 25899)
-- Name: notifications_sent id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications_sent ALTER COLUMN id SET DEFAULT nextval('public.notifications_sent_id_seq'::regclass);


--
-- TOC entry 4733 (class 2604 OID 26105)
-- Name: pending_notifications id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pending_notifications ALTER COLUMN id SET DEFAULT nextval('public.pending_notifications_id_seq'::regclass);


--
-- TOC entry 4718 (class 2604 OID 25781)
-- Name: subjects subject_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subjects ALTER COLUMN subject_id SET DEFAULT nextval('public.subjects_subject_id_seq'::regclass);


--
-- TOC entry 4720 (class 2604 OID 25799)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 4749 (class 2606 OID 25842)
-- Name: admin_tokens admin_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_tokens
    ADD CONSTRAINT admin_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 4747 (class 2606 OID 25819)
-- Name: assignments assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (assignment_id);


--
-- TOC entry 4739 (class 2606 OID 25794)
-- Name: groups groups_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_name_key UNIQUE (name);


--
-- TOC entry 4741 (class 2606 OID 25792)
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (group_id);


--
-- TOC entry 4751 (class 2606 OID 25902)
-- Name: notifications_sent notifications_sent_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications_sent
    ADD CONSTRAINT notifications_sent_pkey PRIMARY KEY (id);


--
-- TOC entry 4755 (class 2606 OID 26109)
-- Name: pending_notifications pending_notifications_chat_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pending_notifications
    ADD CONSTRAINT pending_notifications_chat_id_key UNIQUE (chat_id);


--
-- TOC entry 4757 (class 2606 OID 26107)
-- Name: pending_notifications pending_notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pending_notifications
    ADD CONSTRAINT pending_notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 4735 (class 2606 OID 25785)
-- Name: subjects subjects_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_name_key UNIQUE (name);


--
-- TOC entry 4737 (class 2606 OID 25783)
-- Name: subjects subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_pkey PRIMARY KEY (subject_id);


--
-- TOC entry 4753 (class 2606 OID 26081)
-- Name: notifications_sent unique_notification; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications_sent
    ADD CONSTRAINT unique_notification UNIQUE (assignment_id, chat_id);


--
-- TOC entry 4743 (class 2606 OID 25806)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4745 (class 2606 OID 25808)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4759 (class 2606 OID 25843)
-- Name: admin_tokens admin_tokens_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_tokens
    ADD CONSTRAINT admin_tokens_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(user_id);


--
-- TOC entry 4758 (class 2606 OID 25820)
-- Name: assignments assignments_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(subject_id) ON DELETE CASCADE;


--
-- TOC entry 4760 (class 2606 OID 26082)
-- Name: notifications_sent notifications_sent_assignment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications_sent
    ADD CONSTRAINT notifications_sent_assignment_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(assignment_id) ON DELETE CASCADE;


-- Completed on 2025-04-25 23:32:56

--
-- PostgreSQL database dump complete
--

