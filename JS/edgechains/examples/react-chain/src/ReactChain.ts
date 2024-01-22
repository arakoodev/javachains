// Import necessary modules and libraries
import { Jsonnet } from "@hanazuki/node-jsonnet";
import { OpenAiEndpoint } from "@arakoodev/edgechains.js";
import * as path from "path";
import { Hono } from "hono";
import dotenv from "dotenv"

// Load environment variables from a .env file
dotenv.config();

// Create instances of Jsonnet, Hono, and OpenAiEndpoint
const jsonnet = new Jsonnet();
export const ReactChainRouter = new Hono();
const gpt3Endpoint = new OpenAiEndpoint(
    "https://api.openai.com/v1/chat/completions",
    process.env.OPENAI_API_KEY!,
    "",
    "gpt-4",
    "user",
    parseInt("0.7")
);

// Define file paths for Jsonnet templates
const promptPath = path.join(__dirname, "../src/react-chain.jsonnet");
const InterPath = path.join(__dirname, "../src/intermediate.jsonnet");

// Function to make a GPT-3 call based on a given query
export async function reactChainCall(query: string) {
    try {
        // Load and parse the custom template from react-chain.jsonnet
        const promptLoader = await jsonnet.evaluateFile(promptPath);
        const promptTemplate = JSON.parse(promptLoader).custom_template;

        // Load and parse the intermediate template, injecting the prompt template and query
        let InterLoader = await jsonnet.extString("promptTemplate", promptTemplate)
            .extString("query", query)
            .evaluateFile(InterPath);

        const prompt = JSON.parse(InterLoader).prompt;

        // Make a GPT-3 call using the OpenAiEndpoint
        const gptResponse = await gpt3Endpoint.gptFn(prompt);

        // Replace escaped newline characters with actual newlines
        const formattedResponse = gptResponse.replace(/\\n/g, '\n');
        
        // Return the formatted response
        return formattedResponse;

    } catch (error) {
        // Log and rethrow any errors that occur during the process
        console.error(error);
        throw error;
    }
}

// Function to handle incoming HTTP requests with the given query
export function UserInput(query: string) {
    ReactChainRouter.get("/", async (res) => {
        res.json({ loading: true }); // Respond with a loading status

        try {
            // Call the reactChainCall function with the provided query
            const ReactChainCall = await reactChainCall(query);

            // Replace escaped newline characters with actual newlines in the response
            const formattedResponse = ReactChainCall.replace(/\\n/g, '\n');

            // Respond with the formatted answer
            return res.json({ answer: formattedResponse });

        } catch (error) {
            // If an error occurs, respond with an error status and message
            return res.json({ error: "An error occurred" }, 500);
        }
    });
}

// Example usage: Make a UserInput call with a specific query
UserInput("what is AI");


// let query="Author David Chanoff has collaborated with a U.S. Navy admiral who served as the ambassador to the United Kingdom under which President?"

// "saif"