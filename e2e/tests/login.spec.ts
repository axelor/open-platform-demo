import { expect, test } from "@playwright/test";

test("login failure test", async ({ browser }) => {
  // Create a new browser context without authentication state
  const page = await browser.newPage({
    storageState: undefined,
  });

  // Navigate to the login page
  await page.goto("#/login");

  // Find login page
  const loginPage = page.getByTestId("login-page");
  await expect(loginPage).toBeVisible();

  // Check if the login form is visible
  const loginForm = loginPage.getByTestId("form");
  await expect(loginForm).toBeVisible();

  const user = loginForm.getByTestId("field-username");
  const pass = loginForm.getByTestId("field-password");

  await user.getByTestId("input").fill("wronguser");
  await pass.getByTestId("input").fill("wrongpassword");

  const submit = loginForm.getByTestId("btn-login");
  await submit.click();

  // Verify login was failed
  const error = loginForm.getByTestId("error");
  await expect(error).toBeVisible();
  await expect(error).toHaveText("Wrong username or password");

  await page.close();
});

test("login success test", async ({ browser }) => {
  // Create a new browser context without authentication state
  const page = await browser.newPage({
    storageState: undefined,
  });

  // Navigate to the login page
  await page.goto("#/login");

  // Find login page
  const loginPage = page.getByTestId("login-page");
  await expect(loginPage).toBeVisible();

  // Check if the login form is visible
  const loginForm = loginPage.getByTestId("form");
  await expect(loginForm).toBeVisible();

  const user = loginForm.getByTestId("field-username");
  const pass = loginForm.getByTestId("field-password");

  await user.getByTestId("input").fill("admin");
  await pass.getByTestId("input").fill("admin");

  const submit = loginForm.getByTestId("btn-login");
  await submit.click();

  // Verify login was successful by checking for a logout button
  const navDrawer = page.getByTestId("nav-drawer");
  await expect(navDrawer).toBeVisible();

  // Check user info is displayed
  const appHeader = page.getByTestId("app-header");
  const userMenu = appHeader.getByTestId("user-menu");
  const userItem = userMenu.getByTestId("item:user");
  const userButton = userItem.getByTestId("button");

  await userButton.click();

  const menuControls = await userButton.getAttribute("aria-controls");
  const menu = page.locator(`#${menuControls}`);
  const itemProfile = menu.getByTestId("item:profile");
  await expect(itemProfile).toBeVisible();
  await expect(itemProfile).toContainText("Administrator");

  await page.close();
});
