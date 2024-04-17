import { Hono } from "hono";
const app = new Hono();
// import {pdf} from 'pdf-parse/lib/pdf-parse';
import pdf from "pdf-parse/lib/pdf-parse.js"

// let buffer = readFileSync(STDIO.Stdin)

app.get("/pdf", (c) => {
    // return c.text({"message":"Hello World!"});
    return c.text("hello world")
})

app.get("/", async (c) => {
    try {
        let data = await pdf(buffer, {
            version: 'v2.0.550'
        })  
        // console.log(data.text)
        return c.json(data.text)
    } catch (error) {
        console.log(error) ;  
        console.log("Error occured")
        console.log(err)
        // writeOutput(err)
        let output = JSON.stringify(err)
        console.log(output)
        return c.json(output);
    }
})

app.notFound((c) => {
    return c.text("404 not found", 404);
});

app.fire();