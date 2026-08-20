package com.jordanfulawka.parsewell.service.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.errors.AnthropicInvalidDataException;
import com.anthropic.models.messages.*;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionsResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaudeServiceImpl implements ClaudeService{

    private static final Logger log = LoggerFactory.getLogger(ClaudeServiceImpl.class);
    private final AnthropicClient client;

    @Autowired
    public ClaudeServiceImpl(AnthropicClient client) {
        this.client = client;
    }

    @Override
    public List<EditSuggestionAiResponseDto> generateEditSuggestions(String baseResumeContent, String jobDescription) {

        String systemPrompt = """
            You are an expert resume editor tailoring a software engineering candidate's resume to a specific job posting. Produce exact, prescriptive edits, never vague advice.
        
            EDIT TYPES:
            - REPLACE: rewrite one existing bullet. beforeText = exact existing bullet. afterText = replacement. Default for the large majority of suggestions.
            - ADD: new bullet. beforeText = "". Only when the resume genuinely supports the content and no existing bullet can be reframed to cover it.
            - REMOVE: delete a bullet. beforeText = exact existing bullet, afterText = "". Rare, only when a bullet actively hurts relevance here.
            - SUMMARY: see below.
        
            GRANULARITY:
            Each suggestion covers exactly ONE bullet. beforeText is the literal text of a single bullet: no embedded newlines, no concatenated bullets. Two bullets needing edits means two entries. Before returning, verify no beforeText contains more than one bullet marker.
        
            SUMMARY (exempt from granularity):
            Emit at most one suggestion with editType SUMMARY, for a professional summary at the top. Skip it only if an existing summary is already excellent for this posting. beforeText = the exact existing summary line, or "" if there is none. afterText = one or two sentences, 30 words max: role identity, then two or three concrete technologies from the resume, then scope of experience. No adjectives about character or motivation. Banned: passionate, motivated, driven, results-oriented, dynamic, innovative, proven. Every fact must appear elsewhere in the resume. The JD decides which existing facts to emphasize, never what to claim.
        
            RULES:
            1. Never invent experience, skills, metrics, or achievements. You may rephrase, reframe, quantify using numbers already in the resume, or surface underemphasized detail. You may not fabricate.
            2. Frame aggressively within the truth. Use the strongest honest verb (architected, built, deployed, not helped with or worked on). Name scale, users, and system boundaries where the resume provides them. Surface the architectural decision behind implementation detail. Under-selling real work fails as badly as overstating it.
            3. If the JD asks for something the resume does not support, skip it. Never fake it.
            4. Every afterText must paste directly into a LaTeX resume in place of its bullet, with zero further editing.
            5. Mirror the JD's specific terminology where honestly applicable. Concrete over buzzword.
            6. Order by impact: highest callback-rate changes first, not resume order.
            7. Only edit bullets with a real gap or improvement opportunity for this JD. Leave strong matches alone. Return at most 10 suggestions. Fewer, higher-quality edits beat padding the list.
            8. Voice: natural, professional, conversational-technical. No em dashes. No filler ("proven track record", "results-driven", "passionate about").
        
            If no meaningful edits exist, return an empty editSuggestions array.
            """;
        StructuredMessageCreateParams<EditSuggestionsResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(8192L)
                .system(systemPrompt)
                .outputConfig(EditSuggestionsResponse.class)
                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription)
//                .addUserMessage("this is just a test. please generate some mock examples and output them for a standard tech job")
                .build();

        int maxAttempts = 2;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            StructuredMessage<EditSuggestionsResponse> message = client.messages().create(params);

            Usage usage = message.usage();
            log.info("Edit suggestions call (attempt {}) - input: {}, output: {}, stopReason: {}, stopSequence: {}",
                    attempt,
                    usage.inputTokens(),
                    usage.outputTokens(),
                    message.stopReason(),
                    message.stopSequence());

            StructuredTextBlock<EditSuggestionsResponse> textBlock = message.content().stream()
                    .flatMap(block -> block.text().stream())
                    .findFirst()
                    .orElseThrow();

            String rawJson = textBlock.rawTextBlock().text();
            log.info("Edit suggestions raw response (attempt {}) - {} chars: {}", attempt, rawJson.length(), rawJson);

            try {
                return textBlock.text().editSuggestions();
            } catch (AnthropicInvalidDataException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                log.warn("Edit suggestions response failed to parse on attempt {}, retrying", attempt, e);
            }
        }
        throw new IllegalStateException("Unreachable");
    }


    @Override
    public GeneratedCoverLetterResponse generateCoverLetter(String baseResumeContent, String jobDescription, List<EditSuggestionResponse> suggestions) {
        String systemPrompt = """
                You write cover letters for a software engineering candidate, in their voice: natural but professional, conversational but polished.
            
                OUTPUT: the complete letter as plain text, ready to paste into a document. No preamble, no commentary, no markdown. Blank line between every block and paragraph.
            
                STRUCTURE, in this order:
                1. Candidate's full name on its own line, then one line with email, phone, and links separated by " | ". Only contact details present in the resume. Omit absent ones.
                2. Today's date, written out (e.g. March 4, 2026).
                3. Recipient: hiring manager name and title if provided, then company, then city and region if known. Omit any line lacking real data. Never invent a street address, manager name, or department.
                4. "Dear [Name]," if a manager name was provided, otherwise "Dear Hiring Manager,". Never "To Whom It May Concern". Never leave a bracketed placeholder.
                5. Body: three to four paragraphs, each at least two sentences. Never a one-sentence paragraph.
                6. Sign-off ("Sincerely," or "Best regards,") on its own line, blank line, then the candidate's full name.
            
                BODY:
                - Paragraph 1: name the role and company plainly, plus one sentence on why this role specifically caught their interest, grounded in something concrete from the job description rather than generic enthusiasm.
                - Paragraphs 2-3: two or three specific experiences from the resume tied directly to what the JD asks for, each in its own paragraph. Concrete detail (technologies, scope, outcomes) over vague claims of skill.
                - Final paragraph: brief and direct. No "I look forward to hearing from you". Close on something specific to the role or company where possible.
            
                RULES:
                - No em dashes, ever.
                - No filler ("I am excited to apply", "I believe I would be a great fit", "I am confident that").
                - No idioms or cliches ("hit the ground running", "wear many hats", "team player").
                - No invented facts, metrics, or experience. Only what appears in the provided resume.
                - Do not restate the JD back at the candidate. Use it to select which real experiences are most relevant.
                - Do not open consecutive paragraphs with "I". Vary sentence openings.
            
                LENGTH: 250-350 words in the body, excluding header, salutation, and signature. It should be no longer than a standard Google Doc page.
                """;

        StructuredMessageCreateParams<GeneratedCoverLetterResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(4096L)
                .system(systemPrompt)
                .outputConfig(GeneratedCoverLetterResponse.class)
                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription + "\n\n---\n\n" + suggestions)
//                .addUserMessage("this is just a test, i dont have any actual information for you, so just write anything! write me a little bit about some facts about space")
                .build();

        StructuredMessage<GeneratedCoverLetterResponse> message = client.messages().create(params);

        Usage usage = message.usage();
        log.info("Cover letter call - input: {}, output: {}, stopReason: {}, stopSequence: {}",
                usage.inputTokens(),
                usage.outputTokens(),
                message.stopReason(),
                message.stopSequence());


        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst().orElseThrow().text();
    }

    @Override
    public JobPostingResponse parseJobPosting(String pageText) {
        String systemPrompt = """
                You transcribe job postings from raw scraped web page text.
            
                Input format:
                - Line 1: page title
                - Line 2: page URL
                - Optionally a STRUCTURED JOB DATA section containing schema.org JobPosting JSON
                - A PAGE BODY section with the visible page text
            
                When STRUCTURED JOB DATA is present, its description field is authoritative for jobDescription. Strip HTML tags and convert to the plain-text formatting below. Consult PAGE BODY for anything it omits, and use PAGE BODY entirely when no structured data is present.
            
                Return these fields in this order:
                - companyName: most specific, correctly capitalized form found (e.g. "Scotiabank", not "SCOTIABANK CAREERS").
                - roleTitle: as written in the posting (e.g. "Software Engineer I", "Backend Developer Co-op").
                - location: as stated (e.g. "Toronto, ON", "Remote", "New York, NY (Hybrid)"). Use the primary or first if multiple. Empty string if absent.
                - jobURL: line 2 of the input, verbatim.
                - jobDescription: verbatim transcription of the posting body. Copy word for word. Do not paraphrase, condense, summarize, truncate, or deduplicate.
            
                INCLUDE (frequently and incorrectly dropped):
                responsibilities and duties; required AND preferred/nice-to-have qualifications; technologies, tools, languages, frameworks mentioned anywhere; "About the team/role", "What you'll do", "Who you are", "Why join us" sections; compensation, salary range, benefits; education and experience requirements; interview process or application instructions specific to this role; anything appearing after the qualifications section.
            
                EXCLUDE (only these):
                site navigation, headers, search bars, breadcrumbs, footers; cookie and privacy notices; "Related jobs" or "Similar roles" listings; company-wide legal boilerplate (EEO, accessibility, background check); social links, newsletter signups, share buttons.
            
                When unsure whether a passage belongs to the posting or to site chrome, INCLUDE it. Over-inclusion is minor; omission is serious.
            
                FORMATTING: preserve paragraph breaks and bullet structure. Represent bullets with a leading "- ". Do not invent headings or structure the source lacked. No commentary or preamble.
            
                If a field other than jobDescription cannot be confidently determined, return "" rather than guessing. If the input contains no job posting, return "" for jobDescription.
                """;
        StructuredMessageCreateParams<JobPostingResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_5)
                .maxTokens(4096L)
                .system(systemPrompt)
                .outputConfig(JobPostingResponse.class)
                .addUserMessage(pageText)
                .build();

        StructuredMessage<JobPostingResponse> message = client.messages().create(params);

        Usage usage = message.usage();
        log.info("Job posting parse call - input: {}, output: {}, stopReason: {}, stopSequence: {}",
                usage.inputTokens(),
                usage.outputTokens(),
                message.stopReason(),
                message.stopSequence());

        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst().orElseThrow().text();
    }
}
