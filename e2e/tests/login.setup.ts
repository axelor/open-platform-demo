import { existsSync } from "node:fs";
import { test as setup } from "@playwright/test";
import { STORAGE_STATE } from "../playwright.config";

setup("login", async ({ browser }) => {
  // Reuse existing session if still valid
  if (existsSync(STORAGE_STATE)) {
    const context = await browser.newContext({ storageState: STORAGE_STATE });
    const response = await context.request.get("ws/public/app/info");
    if (response.ok()) {
      const info = await response.json();
      if (info?.user) {
        return;
      }
    }
    await context.close();
  }

  const context = await browser.newContext();
  const page = await context.newPage();

  // Navigate to the login page
  await page.goto("#/login");

  // Find login form
  const loginForm = page.getByTestId("login-page").getByTestId("form");

  const user = loginForm.getByTestId("field-username");
  const pass = loginForm.getByTestId("field-password");

  // Fill in credentials
  await user.getByTestId("input").fill("admin");
  await pass.getByTestId("input").fill("admin");

  // Submit the form
  await loginForm.getByTestId("btn-login").click();

  // Wait for navigation drawer to be visible
  await page.getByTestId("nav-drawer").waitFor({ state: "visible" });

  await context.storageState({ path: STORAGE_STATE });
});
