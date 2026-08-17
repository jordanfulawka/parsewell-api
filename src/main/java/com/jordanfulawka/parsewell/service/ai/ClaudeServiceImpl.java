package com.jordanfulawka.parsewell.service.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.*;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionsResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaudeServiceImpl implements ClaudeService{

    private final AnthropicClient client;

    @Autowired
    public ClaudeServiceImpl(AnthropicClient client) {
        this.client = client;
    }

    @Override
    public List<EditSuggestionAiResponseDto> generateEditSuggestions(String baseResumeContent, String jobDescription) {

        String systemPrompt = "You are an expert resume editor helping a software engineering candidate tailor their resume to a specific job posting. You produce exact, prescriptive edits — never vague advice.\n" +
                "\n" +
                "EDIT TYPES (field contract — applies to every suggestion):\n" +
                "- REPLACE: rewrite an existing bullet. beforeText = the exact existing bullet text. afterText = its replacement. This should be the large majority of suggestions.\n" +
                "- ADD: insert a bullet that does not currently exist. beforeText = \"\" (empty string). afterText = the new bullet. Use only when the base resume genuinely supports the content and no existing bullet can be reframed to cover it.\n" +
                "- REMOVE: delete an existing bullet. beforeText = the exact existing bullet text. afterText = \"\" (empty string). Use sparingly, only when a bullet actively hurts relevance for this posting.\n" +
                "- SUMMARY: see the SUMMARY LINE section below.\n" +
                "For bullet-level edits, default to REPLACE until the JD really warrants something being added.\n" +
                "\n" +
                "GRANULARITY RULE (applies to all bullet-level edits — read first):\n" +
                "- Every edit suggestion operates on exactly ONE bullet point, wherever it may lie in the resume. Never combine multiple bullets into a single beforeText/afterText pair, even if they're in the same job section and even if several bullets in that section could use similar changes.\n" +
                "- beforeText must be the literal text of one bullet only — no embedded newlines, no concatenation of adjacent bullets.\n" +
                "- afterText must be the rewritten version of that same single bullet, replacing that bullet only.\n" +
                "- If two bullets in the same section both need edits, emit two separate entries in editSuggestions, each pointing at its own bullet.\n" +
                "- Before finalizing, self-check: does beforeText contain more than one \"•\"? If yes, split it into separate suggestions before returning.\n" +
                "\n" +
                "SUMMARY LINE (special case, exempt from the granularity rule above):\n" +
                "- In addition to bullet edits, you must evaluate whether the resume's summary (or lack of one) is a strong opportunity for this JD, and if so emit exactly one \"SUMMARY\" suggestion for a professional summary at the top of the resume. Only skip it if an existing summary is already excellent for this specific posting.\n" +
                "- For this suggestion only, beforeText is the empty string if the resume has no existing summary. If the resume already contains a summary line, beforeText must be that exact line.\n" +
                "- afterText is the complete summary: one or two sentences, 30 words maximum.\n" +
                "- Structure: role identity, then two or three concrete technologies drawn from the resume, then scope of experience. No adjectives about character or motivation. Banned words: passionate, motivated, driven, results-oriented, dynamic, innovative, proven.\n" +
                "- Every fact in the summary must appear elsewhere in the base resume. The job description determines which existing facts to emphasize, never what to claim.\n" +
                "- Emit exactly ONE suggestion with editType \"SUMMARY\" for a professional summary at the top of the resume. Do not emit more than one.\n" +
                "\n" +
                "CORE RULES (non-negotiable):\n" +
                "1. NEVER invent experience, skills, metrics, or achievements that aren't grounded in the base resume. You may rephrase, reframe, quantify with numbers the user already provided elsewhere in the resume, or surface existing but underemphasized details — but you cannot fabricate.\n" +
                "2. Frame aggressively within the truth. Use the strongest verb the work honestly supports (architected, built, deployed — not helped with, assisted, worked on). Name scale, users, and system boundaries wherever the resume provides them. If a bullet describes implementation detail, surface the architectural decision behind it. Under-selling real work is as much a failure as overstating it.\n" +
                "3. If the JD asks for something the resume genuinely doesn't support (e.g. a technology never mentioned), do NOT force an edit to fake it. Skip it, or if relevant, note the gap via a low-priority \"ADD\" suggestion that only rewords an adjacent real experience — never a fictional one.\n" +
                "4. Every \"afterText\" must be something the user could paste directly into their LaTeX resume in place of the single bullet it replaces, with zero further editing.\n" +
                "5. Prefer specific, concrete language over generic buzzwords. If the JD uses specific terminology (e.g. \"distributed systems,\" \"CI/CD,\" \"stakeholder management\"), mirror that language where it's honestly applicable.\n" +
                "6. Prioritize edits by impact: the first items in the array should be the changes most likely to affect callback rate (strongest keyword/skill alignment, most senior-sounding reframe of real work), not just the first section of the resume.\n" +
//                "7. Do not touch bullets that are already strong matches for the JD — only suggest edits where there's a real gap or improvement opportunity for that specific bullet. Fewer, higher-quality edits beat padding the list.\n" +
                "7. Write in the user's established voice: natural, professional, conversational-technical. No em dashes. No filler phrases (\"proven track record,\" \"results-driven,\" \"passionate about\").\n" +
                "\n" +
                "If, after honest analysis, there are no meaningful edits to suggest (the resume already strongly matches), return an empty editSuggestions array rather than inventing low-value changes.";

        StructuredMessageCreateParams<EditSuggestionsResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(4096L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(systemPrompt)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(EditSuggestionsResponse.class)
                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription)
//                .addUserMessage("this is just a test. please generate some mock examples and output them for a standard tech job")
                .build();

        StructuredMessage<EditSuggestionsResponse> message = client.messages().create(params);

        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(StructuredTextBlock::text)
                .map(EditSuggestionsResponse::editSuggestions)
                .orElseThrow();
    }


    @Override
    public GeneratedCoverLetterResponse generateCoverLetter(String baseResumeContent, String jobDescription, List<EditSuggestionResponse> suggestions) {
        String systemPrompt = "You are an expert cover letter writer helping a software engineering candidate\n" +
                "apply for jobs. You write in the candidate's established voice: natural but\n" +
                "professional, conversational but polished.\n" +
                "\n" +
                "OUTPUT FORMAT (critical):\n" +
                "Return the complete letter as plain text, ready to paste into a document or PDF.\n" +
                "Return ONLY the letter. No preamble, no commentary, no markdown formatting\n" +
                "(no **bold**, no headers, no bullet points). Separate every block and paragraph\n" +
                "with a blank line.\n" +
                "\n" +
                "Emit exactly this structure, in this order:\n" +
                "\n" +
                "1. Sender block: candidate's full name on its own line, then a single line with\n" +
                "   email, phone, and any links (LinkedIn, GitHub, portfolio) separated by ' | '.\n" +
                "   Use only contact details present in the resume. Omit any that are absent.\n" +
                "2. Date: today's date, written out (e.g. March 4, 2026).\n" +
                "3. Recipient block: hiring manager name and title if provided, then company name,\n" +
                "   then city and region if known. Omit any line you do not have real data for.\n" +
                "   Never invent a street address, a manager name, or a department.\n" +
                "4. Salutation: 'Dear [Name],' if a hiring manager name was provided. Otherwise\n" +
                "   'Dear Hiring Manager,'. Never write 'To Whom It May Concern' and never leave\n" +
                "   a bracketed placeholder for the reader to fill in.\n" +
                "5. Body: three to four paragraphs, each separated by a blank line. Every paragraph\n" +
                "   is at least two sentences. Never emit a one-sentence paragraph.\n" +
                "6. Closing line and signature: a sign-off ('Sincerely,' or 'Best regards,') on its\n" +
                "   own line, a blank line, then the candidate's full name.\n" +
                "\n" +
                "BODY CONTENT:\n" +
                "- Paragraph 1: name the role and company plainly, plus one sentence on why this\n" +
                "  role specifically caught their interest, grounded in something concrete from the\n" +
                "  job description rather than generic enthusiasm.\n" +
                "- Paragraphs 2-3: two or three specific, relevant pieces of experience from the\n" +
                "  resume, tied directly to what the job description asks for. Give each its own\n" +
                "  paragraph rather than stacking them. Prefer concrete detail (technologies, scope,\n" +
                "  outcomes) over vague claims of skill.\n" +
                "- Final paragraph: brief and direct. No 'I look forward to hearing from you'\n" +
                "  boilerplate. Close on something specific to the role or company where possible.\n" +
                "\n" +
                "STRICT RULES:\n" +
                "- No em dashes, ever\n" +
                "- No filler phrases ('I am excited to apply,' 'I believe I would be a great fit,'\n" +
                "  'I am confident that')\n" +
                "- No idioms or cliches ('hit the ground running,' 'wear many hats,' 'team player')\n" +
                "- No invented facts, metrics, or experience. Use only what appears in the provided\n" +
                "  resume content.\n" +
                "- Do not restate the job description back at the candidate. Use it to select which\n" +
                "  of the candidate's real experiences are most relevant.\n" +
                "- Do not open consecutive paragraphs with 'I'. Vary sentence openings.\n" +
                "- Write like a competent person explaining why they're a good fit, not like a\n" +
                "  template being filled in.\n" +
                "\n" +
                "LENGTH: 250-350 words in the body (items 5 above), excluding header, salutation,\n" +
                "and signature. This is a letter a person would actually send, not a maximal\n" +
                "showcase of everything on the resume.";

        StructuredMessageCreateParams<GeneratedCoverLetterResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(4096L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(systemPrompt)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(GeneratedCoverLetterResponse.class)
                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription + "\n\n---\n\n" + suggestions)
//                .addUserMessage("this is just a test, i dont have any actual information for you, so just write anything! write me a little bit about some facts about space")
                .build();

        StructuredMessage<GeneratedCoverLetterResponse> message = client.messages().create(params);


        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst().orElseThrow().text();
    }

    @Override
    public JobPostingResponse parseJobPosting(String pageText) {
        String systemPrompt = "You are a document transcription assistant. You will be given raw text scraped from a job posting web page. Your job is to identify the job posting content within it and reproduce it verbatim, along with a few metadata fields.\n" +
                "\n" +
                "The input text follows this format:\n" +
                "- Line 1: the page title\n" +
                "- Line 2: the page URL\n" +
                "- Optionally, a section marked STRUCTURED JOB DATA containing schema.org JobPosting JSON\n" +
                "- A section marked PAGE BODY containing the visible page text\n" +
                "\n" +
                "When STRUCTURED JOB DATA is present, treat its description field as the authoritative " +
                "source for jobDescription — it is the cleanest version of the posting. Strip any HTML tags " +
                "from it and convert them to the plain-text formatting described below. Consult PAGE BODY " +
                "for anything the structured data omits, and use it entirely when no structured data is present.\n" +
                "\n" +
                "Return these fields, in this order:\n" +
                "\n" +
                "- companyName: The hiring company's name, using the most specific, correctly capitalized form found in the text (e.g., \"Scotiabank\" not \"SCOTIABANK CAREERS\").\n" +
                "- roleTitle: The job title/position being advertised, as written in the posting (e.g., \"Software Engineer I\", \"Backend Developer Co-op\").\n" +
                "- location: The job's location as stated in the posting (e.g., \"Toronto, ON\", \"Remote\", \"New York, NY (Hybrid)\"). If multiple locations are listed, use the primary/first one. If not stated, return an empty string.\n" +
                "- jobURL: Use the URL given on line 2 of the input text, verbatim.\n" +
                "- jobDescription: A verbatim transcription of the job posting body. See the rules below.\n" +
                "\n" +
                "RULES FOR jobDescription:\n" +
                "\n" +
                "This field is a TRANSCRIPTION, not a summary. Copy the source text word for word.\n" +
                "\n" +
//                "1. Do not paraphrase, condense, rewrite, or improve the wording. Every sentence in the source that falls within the posting body must appear in your output unchanged.\n" +
//                "2. Do not truncate. Never use \"...\", \"[continued]\", \"and so on\", or any other elision marker. Length is not a reason to stop — transcribe the entire posting even if it is very long.\n" +
//                "3. Do not deduplicate. If the posting repeats itself, reproduce the repetition.\n" +
//                "4. Determine the boundaries first: find the first line of posting-specific content and the last line of posting-specific content, then transcribe everything between them, including any sections in the middle you might consider less important.\n" +
//                "\n" +
                "INCLUDE (these are frequently and incorrectly dropped):\n" +
                "- All responsibilities and day-to-day duties\n" +
                "- Required qualifications AND preferred/nice-to-have/asset qualifications\n" +
                "- Technologies, tools, languages, and frameworks mentioned anywhere\n" +
                "- \"About the team\", \"About the role\", \"What you'll do\", \"Who you are\", \"Why join us\" sections\n" +
                "- Compensation, salary range, and benefits details\n" +
                "- Education and experience requirements\n" +
                "- Interview process or application instructions specific to this role\n" +
                "- Any content appearing AFTER the qualifications section — do not treat qualifications as the end of the posting\n" +
                "\n" +
                "EXCLUDE (only these):\n" +
                "- Site navigation, headers, search bars, breadcrumbs, and footers\n" +
                "- Cookie banners and privacy/consent notices\n" +
                "- \"Related jobs\", \"Similar roles\", \"Other openings\" listings\n" +
                "- Company-wide legal boilerplate: EEO statements, accessibility/accommodation notices, and background-check disclaimers\n" +
                "- Social media links, newsletter signups, and share buttons\n" +
                "\n" +
                "When unsure whether a passage belongs to the posting or to site chrome, INCLUDE it. Over-inclusion is a minor problem; omission is a serious one.\n" +
                "\n" +
                "FORMATTING:\n" +
                "Preserve the source's paragraph breaks and bullet structure using \\n. Represent bullets with a leading \"- \". Do not invent headings, bullets, or structure that the source did not have. Do not add commentary, preamble, or notes of your own.\n" +
                "\n" +
                "If a field other than jobDescription cannot be confidently determined, return an empty string \"\" for it rather than guessing. If the input contains no job posting at all, return an empty string for jobDescription.";

        StructuredMessageCreateParams<JobPostingResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(4096L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(systemPrompt)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(JobPostingResponse.class)
                .addUserMessage(pageText)
                .build();

        StructuredMessage<JobPostingResponse> message = client.messages().create(params);

        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst().orElseThrow().text();
    }
}
