import { expect, test, Page } from "@playwright/test";

import { FormView } from "./pages/form-view";
import { GridView } from "./pages/grid-view";

async function createProduct(
  page: Page,
  { name, code }: { name: string; code: string },
) {
  const viewForm = page.getByTestId("view:form");
  await expect(viewForm).toBeVisible();

  await viewForm.getByTestId("field:name").getByRole("textbox").fill(name);
  await viewForm.getByTestId("field:code").getByRole("textbox").fill(code);

  return new FormView(page);
}

test("create new product", async ({ page }) => {
  const uniqueId = Date.now();
  const name = `Test Product ${uniqueId}`;
  const code = `TestProduct${uniqueId}`;

  await page.goto("#/ds/sale.products/list/1");

  const tab = page.locator('[data-view-id="sale.products"]');
  await expect(tab).toBeVisible();

  // Click on New button in the toolbar
  const grid = new GridView(page);
  await grid.newRecord();

  // Wait for the edit form to appear
  const form = await createProduct(page, { name, code });

  // numeric field
  const productPrice = form.getField("price").getByTestId("input");
  await productPrice.fill("1200.5");

  // SwitchSelect field
  const productType = form.getField("type").getByTestId("option-PRODUCT");
  await productType.click();

  // SingleSelect field
  const productColor = form.getField("color").getByRole("combobox");
  await productColor.click();

  const colorList = page.getByRole("listbox", { name: "Select options" });
  await colorList.getByRole("option", { name: "Red" }).click();

  // M2O field
  await form.getField("category").getByTestId("icon:find").click();
  const modalSearchGrid = page.getByRole("dialog");
  await expect(modalSearchGrid).toBeVisible();

  const nameSearchCell = page
    .getByTestId("search-row")
    .getByTestId("column:name")
    .getByRole("textbox");
  await nameSearchCell.fill("Books");
  await nameSearchCell.press("Enter");

  await expect(
    modalSearchGrid.getByTestId("grid").getByTestId("body").getByRole("row"),
  ).toHaveCount(1);
  await modalSearchGrid
    .getByTestId("grid")
    .getByTestId("body")
    .getByRole("gridcell", { name: "Select row" })
    .getByRole("radio")
    .check();
  await expect(modalSearchGrid).toBeHidden();

  await expect(form.getField("category").getByTestId("input")).toHaveValue(
    "Books",
  );

  // M2M tags field
  const extraOptionsField = form.getField("extraOptions").getByRole("combobox");
  await extraOptionsField.pressSequentially("warranty");
  const extraOptionsList = page.getByRole("listbox", {
    name: "Select options",
  });
  await expect(extraOptionsList.getByRole("option")).toHaveCount(4);
  await extraOptionsList
    .getByRole("option", { name: "3 years warranty" })
    .click();
  await expect(form.getField("extraOptions").getByTestId(/^tag:/)).toHaveCount(
    1,
  );

  // Save the sale order
  await form.save();

  await expect(page).toHaveURL(/edit\/\d+/);
});

test("create duplicated product should display error message", async ({
  page,
}) => {
  const uniqueId = Date.now();
  const name = `Duplicate Product ${uniqueId}`;
  const code = `DuplicateProduct${uniqueId}`;

  // Create the first product
  await page.goto("#/ds/sale.products/edit");
  let form = await createProduct(page, { name, code });
  await form.save();
  await expect(page).toHaveURL(/edit\/\d+/);

  // Try to create a second product with the same name and code
  await form.newRecord();
  form = await createProduct(page, { name, code });
  await form.save();

  await expect(page.getByRole("dialog").getByTestId("body")).toContainText(
    "The record(s) can't be updated as it violates unique constraint.",
  );
});

test("print products catalog", async ({ page }) => {
  await page.goto("#/ds/sale.products/list/1");

  await page
    .getByTestId("toolbar")
    .getByRole("button", { name: "Print catalog" })
    .click();

  const tabs = page.locator("[data-tab-content]");
  await expect(tabs).toHaveCount(2);

  const iframe = tabs.last().locator("iframe");
  await expect(iframe).toHaveAttribute("src", /\.pdf$/);
});

test("search usb hub product from column", async ({ page }) => {
  await page.goto("#/ds/sale.products/list/1");

  const nameCol = page
    .getByTestId("search-row")
    .getByTestId("column:name")
    .getByRole("textbox");
  await nameCol.fill("usb hub");
  await nameCol.press("Enter");

  const rows = page.getByTestId("grid").getByTestId("body").getByRole("row");

  await expect(rows).toHaveCount(1);
  await expect(rows.first().getByTestId("column:name")).toHaveText(
    "7-Port USB Hub",
  );
});

test("search usb hub product from advance search", async ({ page }) => {
  await page.goto("#/ds/sale.products/list/1");

  const grid = new GridView(page);
  const advanceSearch = await grid.openAdvanceSearch();

  const fieldInput = advanceSearch.getByTestId("field").getByRole("combobox");
  const fieldId = await fieldInput.getAttribute("aria-controls");
  await fieldInput.click();
  await page.locator(`#${fieldId}`).getByTestId("option:name").click();

  const operatorInput = advanceSearch
    .getByTestId("operator")
    .getByRole("combobox");
  const operatorId = await operatorInput.getAttribute("aria-controls");
  await operatorInput.focus();
  await page
    .locator(`#${operatorId}`)
    .getByRole("option", { name: "equals" })
    .click();

  await advanceSearch
    .getByTestId("value")
    .getByRole("textbox")
    .fill("7-Port USB Hub");

  await advanceSearch.getByTestId("btn-apply").click();

  await expect(advanceSearch).not.toBeVisible();

  const rows = page.getByTestId("grid").getByTestId("body").getByRole("row");

  await expect(rows).toHaveCount(1);
  await expect(rows.first().getByTestId("column:name")).toHaveText(
    "7-Port USB Hub",
  );
});
