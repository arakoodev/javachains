function get_package_json(options) {
    return (
        JSON.stringify(
            {
                name: "example",
                version: "1.0.0",
                description: "",
                main: "dist/index.js",
                scripts: {
                    build: "rm -rf dist && node esbuild.build.cjs",
                    start: "node dist/index.js",
                    lint: "eslint --ignore-path .eslintignore --ext .js,.ts",
                    format: 'prettier --ignore-path .gitignore --write "**/*.+(js|ts|json)"',
                    test: "npx jest",
                },
                keywords: [],
                author: "",
                license: "ISC",
                dependencies: {
                    "@hono/node-server": "^1.2.0",
                    "@types/dotenv": "^8.2.0",
                    hono: "^3.9.2",
                    pg: "^8.11.3",
                    "reflect-metadata": "^0.1.13",
                    tsc: "^2.0.4",
                    typescript: "^5.3.2",
                },
                devDependencies: {
                    "@hanazuki/node-jsonnet": "^2.1.0",
                    "@types/jest": "^29.5.8",
                    "@types/node": "^20.9.4",
                    "@typescript-eslint/eslint-plugin": "^6.11.0",
                    "@typescript-eslint/parser": "^6.11.0",
                    axios: "^1.6.2",
                    dotenv: "^16.3.1",
                    "dts-bundle-generator": "^8.1.2",
                    eslint: "^8.54.0",
                    "eslint-config-prettier": "^9.0.0",
                    "eslint-config-standard-with-typescript": "^40.0.0",
                    "eslint-plugin-import": "^2.29.0",
                    "eslint-plugin-n": "^16.3.1",
                    "eslint-plugin-promise": "^6.1.1",
                    jest: "^29.7.0",
                    prettier: "^3.1.0",
                    react: "^18.2.0",
                    "@arakoodev/edgechains.js": "0.1.22",
                    "ts-jest": "^29.1.1",
                    tsx: "^3.12.2",
                    typeorm: "^0.3.17",
                    typescript: "^5.0.2",
                },
            },
            null,
            2
        ) + "\n"
    );
}
export { get_package_json };
