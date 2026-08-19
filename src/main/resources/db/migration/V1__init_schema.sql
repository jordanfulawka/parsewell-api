CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    email varchar(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE base_resumes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE references users(id) ON DELETE CASCADE,
    content TEXT,
    file_name TEXT,
    original_file_url TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TYPE application_channel_type AS ENUM('COLD_APPLICATION', 'REFERRAL', 'DIRECT_OUTREACH', 'OTHER');
CREATE type application_status_type AS ENUM('APPLIED', 'HEARD_BACK', 'REJECTED', 'GHOSTED', 'DRAFT');

CREATE TABLE applications (
    id UUID PRIMARY KEY,
    user_id UUID references users(id) ON DELETE CASCADE,
    base_resume_id UUID references base_resumes(id) ON DELETE SET NULL,
    company_name varchar(255),
    role_title varchar(255),
    location varchar(255),
    job_url varchar(255),
    job_description TEXT,
    application_channel application_channel_type,
    application_status application_status_type,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TYPE edit_type AS ENUM('REPLACE', 'ADD', 'REMOVE', 'SUMMARY');

CREATE TABLE edit_suggestions (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL references applications(id) ON DELETE CASCADE,
    section TEXT,
    before_text TEXT,
    after_text TEXT,
    reason TEXT,
    edit_type edit_type,
    order_index INT NOT NULL
);

CREATE TABLE final_materials (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL UNIQUE references applications(id) ON DELETE CASCADE,
    resume_key varchar(255),
    resume_filename varchar(255),
    cover_letter_key varchar(255),
    cover_letter_filename varchar(255),
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE generated_cover_letters (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL UNIQUE references applications(id) ON DELETE CASCADE,
    content TEXT,
    generated_at TIMESTAMP NOT NULL
);
