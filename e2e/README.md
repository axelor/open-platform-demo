

# E2E Tests Example

This project is an example showing how to write end-to-end (E2E) browser-based tests for Axelor platform apps using [Playwright](https://playwright.dev/). It helps ensure your application works as expected by automating user interactions and verifying outcomes.

## Prerequisites

- Node.js (v16+ recommended)
- [pnpm](https://pnpm.io/) (preferred) or npm/yarn
- Chromium/Chrome, Firefox, or WebKit browsers (Playwright can install these automatically)

## Installation

```sh
pnpm install
```

## Running Tests

To run all tests:

```sh
pnpm test
```

To run a specific test file:

```sh
pnpm test tests/login.spec.ts
```

To view the Playwright HTML report after running tests:

```sh
pnpm test --reporter=html
pnpm exec playwright show-report
```

## Directory Structure

```
e2e/
├── eslint.config.js
├── package.json
├── playwright.config.ts
├── tsconfig.json
├── tests/
│   ├── login.setup.ts
│   ├── login.spec.ts
│   ├── sales.spec.ts
├── playwright-report/
├── test-results/
└── README.md
```

- `tests/`: Contains all Playwright test files (excluding experimental or placeholder files).
- `playwright.config.ts`: Playwright configuration.
- `playwright-report/`: Generated test reports.
- `test-results/`: Raw test output and traces.

## Customization & Configuration

- Edit `playwright.config.ts` to change test settings (browser, base URL, timeouts, etc).
- Add new test files in the `tests/` directory.
- Use Playwright’s CLI for advanced options:  
  `npx playwright --help`

## Troubleshooting

- If browsers are missing, run:  
  `pnpm exec playwright install`
- For debugging, use:  
  `pnpm test --debug`
- For trace viewer:  
  `pnpm exec playwright show-trace test-results/<trace-folder>`
