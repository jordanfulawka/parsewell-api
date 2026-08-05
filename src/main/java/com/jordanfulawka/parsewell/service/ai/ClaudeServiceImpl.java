package com.jordanfulawka.parsewell.service.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.*;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionsResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
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
                "CORE RULES (non-negotiable):\n" +
                "1. NEVER invent experience, skills, metrics, or achievements that aren't grounded in the base resume. You may rephrase, reframe, quantify with numbers the user already provided elsewhere in the resume, or surface existing but underemphasized details — but you cannot fabricate.\n" +
                "2. If the JD asks for something the resume genuinely doesn't support (e.g. a technology never mentioned), do NOT force an edit to fake it. Skip it, or if relevant, note the gap via a low-priority \"ADD\" suggestion that only rewords an adjacent real experience — never a fictional one.\n" +
                "3. Every \"afterText\" must be something the user could paste directly into their LaTeX resume with zero further editing.\n" +
                "4. Prefer specific, concrete language over generic buzzwords. If the JD uses specific terminology (e.g. \"distributed systems,\" \"CI/CD,\" \"stakeholder management\"), mirror that language where it's honestly applicable.\n" +
                "5. Prioritize edits by impact: the first items in the array should be the changes most likely to affect callback rate (strongest keyword/skill alignment, most senior-sounding reframe of real work), not just the first section of the resume.\n" +
                "6. Do not touch sections of the resume that are already strong matches for the JD — only suggest edits where there's a real gap or improvement opportunity. Fewer, higher-quality edits beat padding the list.\n" +
                "7. Write in the user's established voice: natural, professional, conversational-technical. No em dashes. No filler phrases (\"proven track record,\" \"results-driven,\" \"passionate about\").\n" +
                "\n" +
                "If, after honest analysis, there are no meaningful edits to suggest (the resume already strongly matches), return an empty editSuggestions array rather than inventing low-value changes.";

        StructuredMessageCreateParams<EditSuggestionsResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_HAIKU_4_5)
                .maxTokens(4096L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(systemPrompt)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(EditSuggestionsResponse.class)
//                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription)
                .addUserMessage("this is just a test. please generate some mock examples and output them for a standard tech job")
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
        String systemPrompt = "You are an expert cover letter writer helping a software engineering candidate \n" +
                "apply for jobs. You write in the candidate's established voice: natural but \n" +
                "professional, conversational but polished. \n" +
                "\n" +
                "Strict rules:\n" +
                "- No em dashes, ever\n" +
                "- No filler phrases (\"I am excited to apply,\" \"I believe I would be a great fit,\" \n" +
                "  \"I am confident that\")\n" +
                "- No idioms or clichés (\"hit the ground running,\" \"wear many hats,\" \"team player\")\n" +
                "- No invented facts, metrics, or experience — use only what appears in the \n" +
                "  provided resume content\n" +
                "- Do not restate the job description back at the candidate; use it to select \n" +
                "  which of the candidate's real experiences are most relevant\n" +
                "- Write like a competent person explaining why they're a good fit, not like a \n" +
                "  template being filled in\n" +
                "\n" +
                "Structure:\n" +
                "- Opening: state the role and company plainly, one sentence on why this role \n" +
                "  specifically caught their interest (grounded in something concrete from the \n" +
                "  job description, not generic enthusiasm)\n" +
                "- Body (1-2 paragraphs): 2-3 specific, relevant pieces of experience from the \n" +
                "  resume, tied directly to what the job description asks for. Prefer concrete \n" +
                "  detail (technologies, scope, outcomes) over vague claims of skill\n" +
                "- Closing: brief, direct. No \"I look forward to hearing from you\" boilerplate — \n" +
                "  something more specific to the role/company if possible\n" +
                "\n" +
                "Length: 250-350 words. This is a letter a person would actually send, not a \n" +
                "maximal showcase of everything on the resume.";

        StructuredMessageCreateParams<GeneratedCoverLetterResponse> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_HAIKU_4_5)
                .maxTokens(4096L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(systemPrompt)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(GeneratedCoverLetterResponse.class)
//                .addUserMessage(baseResumeContent + "\n\n---\n\n" + jobDescription + "\n\n---\n\n" + suggestions)
                .addUserMessage("this is just a test, i dont have any actual information for you, so just write anything! write me a little bit about some facts about space")
                .build();

        StructuredMessage<GeneratedCoverLetterResponse> message = client.messages().create(params);

        System.out.println(message);

        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst().orElseThrow().text();
    }
}
