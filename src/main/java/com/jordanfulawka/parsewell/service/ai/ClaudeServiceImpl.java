package com.jordanfulawka.parsewell.service.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.jordanfulawka.parsewell.dto.EditSuggestionAiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaudeServiceImpl implements ClaudeService{


    @Value("${anthropic.apiKey}")
    private String apiKey;

    @Override
    public List<EditSuggestionAiResponseDto> generateEditSuggestions(String baseResumeContent, String jobDescription) {

        AnthropicClient client = AnthropicOkHttpClient.builder().apiKey(apiKey).build();

        String prompt = "You are an expert resume editor helping a software engineering candidate tailor their resume to a specific job posting. You produce exact, prescriptive edits — never vague advice.\n" +
                "\n" +
                "CORE RULES (non-negotiable):\n" +
                "1. NEVER invent experience, skills, metrics, or achievements that aren't grounded in the base resume. You may rephrase, reframe, quantify with numbers the user already provided elsewhere in the resume, or surface existing but underemphasized details — but you cannot fabricate.\n" +
                "2. If the JD asks for something the resume genuinely doesn't support (e.g. a technology never mentioned), do NOT force an edit to fake it. Skip it, or if relevant, note the gap via a low-priority \"ADD\" suggestion that only rewords an adjacent real experience — never a fictional one.\n" +
                "3. Every \"afterText\" must be something the user could paste directly into their LaTeX resume with zero further editing. No placeholders like \"[X]%\" or \"quantify here.\"\n" +
                "4. Prefer specific, concrete language over generic buzzwords. If the JD uses specific terminology (e.g. \"distributed systems,\" \"CI/CD,\" \"stakeholder management\"), mirror that language where it's honestly applicable.\n" +
                "5. Prioritize edits by impact: the first items in the array should be the changes most likely to affect callback rate (strongest keyword/skill alignment, most senior-sounding reframe of real work), not just the first section of the resume.\n" +
                "6. Do not touch sections of the resume that are already strong matches for the JD — only suggest edits where there's a real gap or improvement opportunity. Fewer, higher-quality edits beat padding the list.\n" +
                "7. Write in the user's established voice: natural, professional, conversational-technical. No em dashes. No filler phrases (\"proven track record,\" \"results-driven,\" \"passionate about\").\n" +
                "\n" +
                "OUTPUT FORMAT:\n" +
                "Return ONLY valid JSON, no preamble, no markdown code fences, no explanation text. Match this exact schema:\n" +
                "\n" +
                "{\n" +
                "  \"editSuggestions\": [\n" +
                "    {\n" +
                "      \"section\": \"string — e.g. 'Experience - Kenna', 'Summary', 'Skills', 'Projects - Gather'\",\n" +
                "      \"beforeText\": \"string — exact current text from the base resume, or empty string if this is a net-new ADD\",\n" +
                "      \"afterText\": \"string — the exact replacement/new text, copy-paste ready\",\n" +
                "      \"reason\": \"string — one sentence tying this edit to a specific part of the JD\",\n" +
                "      \"editType\": \"REPLACE | ADD | REMOVE\",\n" +
                "      \"orderIndex\": integer, starting at 0, in priority order\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "\n" +
                "If, after honest analysis, there are no meaningful edits to suggest (the resume already strongly matches), return an empty editSuggestions array rather than inventing low-value changes.";

        MessageCreateParams params = MessageCreateParams.builder()
                .maxTokens(1024L)
                .addUserMessage(prompt)
                .model(Model.CLAUDE_HAIKU_4_5)
                .build();

        Message message = client.messages().create(params);
        System.out.println(message.content());
        return message;
    }

    @Override
    public String generateCoverLetter(String baseResumeContent, String jobDescription) {
        return "";
    }
}
