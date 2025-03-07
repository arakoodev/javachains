import { ArakooServer } from "@arakoodev/edgechains.js/arakooserver";
import { ReactChainRouter } from "./routes/react-chain.js";

const server = new ArakooServer();

const app = server.createApp();

app.route("/", ReactChainRouter);

server.listen(5000);
