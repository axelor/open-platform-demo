import { expect, test } from "@playwright/test";
import { GridView } from "./pages/grid-view";
import { FormView } from "./pages/form-view";

test.beforeEach(async ({ page }) => {
  await page.goto("#/");
});

test("open sale order list", async ({ page }) => {
  const drawer = page.getByTestId("nav-drawer");
  await expect(drawer).toBeVisible();

  const menu = drawer.getByTestId("menus").getByTestId("item:menu-sales");
  await menu.click();

  const item = menu.getByTestId("item:menu-sales-order");
  await item.click();

  const tabs = page.getByTestId("nav-tabs");
  await expect(tabs).toBeVisible();

  const tab = tabs.getByTestId("tab:sale.orders");
  await expect(tab).toBeVisible();

  const panel = page.getByTestId("tab-panel:sale.orders");
  await expect(panel).toBeVisible();

  const view = panel.getByTestId("view:grid");
  await expect(view).toBeVisible();
});

test("create new sale order", async ({ page }) => {
  const menu = page
    .getByTestId("nav-drawer")
    .getByTestId("menus")
    .getByTestId("item:menu-sales");
  await menu.click();
  await menu.getByTestId("item:menu-sales-order").click();

  const tab = page.getByTestId("tab-panel:sale.orders");
  await expect(tab).toBeVisible();

  const viewGrid = tab.getByTestId("view:grid");
  await expect(viewGrid).toBeVisible();

  // Click on New button in the toolbar
  const grid = new GridView(page);
  await grid.newRecord();

  // Wait for the edit form to appear
  const viewForm = tab.getByTestId("view:form");
  await expect(viewForm).toBeVisible();

  const form = new FormView(page);

  // Select customer field
  const customerInput = form.getField("customer").getByRole("combobox");
  const dropdownId = await customerInput.getAttribute("aria-controls");
  await customerInput.focus(); // Trigger the dropdown

  const customerList = page.locator(`#${dropdownId}`);
  await expect(customerList).toBeVisible();

  // Find role="option" with text "Miss Ansh Kaya"
  const customerOption = customerList
    .getByRole("option")
    .filter({ hasText: "Miss Ansh Kaya" });

  // Select the customer
  await customerOption.click();

  // Find the order lines
  const orderLines = form.getField("items");
  await expect(orderLines).toBeVisible();

  // Find the first row in the order lines grid
  const firstRow = orderLines
    .getByTestId("grid")
    .getByTestId("body")
    .getByRole("row")
    .first();

  await expect(firstRow).toBeVisible();

  // Find the product cell
  const productCell = firstRow.getByTestId("column:product");

  // Click on the product cell to activate the select input
  await productCell.click(); // will trigger edit mode and open the select list

  // Find the select input inside the product cell
  const productField = productCell
    .getByTestId("field:product")
    .getByRole("combobox");
  const productListId = await productField.getAttribute("aria-controls");
  await expect(productField).toBeVisible();

  // Find the product list
  const productList = page.locator(`#${productListId}`);
  await expect(productList).toBeVisible();

  // Find role="option" with text "14-inch Ultrabook Laptop"
  const productOption = productList
    .getByRole("option")
    .filter({ hasText: "14-inch Ultrabook Laptop" });

  await expect(productOption).toBeVisible();

  // Select the product
  await productOption.click();

  // Wait for the row to update
  await expect(productField).toHaveValue("14-inch Ultrabook Laptop");

  // Set price to 1200
  const priceCell = firstRow.getByTestId("column:price");
  const priceInput = priceCell.getByTestId("field:price").getByTestId("input");
  await expect(priceInput).toHaveValue("899.9900");
  await priceInput.fill("1200");

  // Set quantity to 2
  const quantityCell = firstRow.getByTestId("column:quantity");
  const quantityInput = quantityCell
    .getByTestId("field:quantity")
    .getByTestId("input");
  await quantityInput.fill("2");

  // Save the sale order
  await form.save();

  // Wait for save to complete
  await expect(page).toHaveURL(/edit\/\d+/); // URL should now contain /{id}

  // Check total amount
  const totalAmount = form.getField("totalAmount");
  await expect(totalAmount).toContainText("2,400.00"); // 14-inch Ultrabook Laptop at 1200 x 2 = 2400

  // Delete the created sale order to clean up
  await form.delete();

  const dialog = page.getByTestId(/^dialog:\d+$/).last();
  await expect(dialog).toBeVisible();

  // Confirm deletion
  await dialog.getByTestId("btn-confirm").click();

  // After delete, tab returns to list view
  await expect(viewGrid).toBeVisible();
});

test("update SO00001 sale order", async ({ page }) => {
  await page.goto("#/ds/sale.orders.draft/list/1");

  // open first sale order
  const gridRows = page
    .getByTestId("grid")
    .getByTestId("body")
    .getByRole("row");

  await expect(gridRows.first()).toBeVisible();
  await gridRows.first().getByTestId("column:$$edit").click();

  await expect(page.getByTestId("view:form")).toBeVisible();

  const form = new FormView(page);

  const orderLines = form.getField("items");
  await expect(
    page.getByTestId("grid").getByTestId("body").getByRole("row").first(),
  ).toBeVisible();
  const rows = await orderLines
    .getByTestId("grid")
    .getByTestId("body")
    .getByRole("row")
    .all();

  // Update each item
  const qty = Math.floor(Math.random() * 100) + 1;
  const unitPrice = Math.floor(Math.random() * 100) + 1;

  for (let index = 0; index < rows.length; index++) {
    const row = rows[index];

    const priceCell = row.getByTestId("column:price");
    await priceCell.click();
    const priceInput = priceCell
      .getByTestId("field:price")
      .getByTestId("input");
    await priceInput.fill(unitPrice.toString());

    const quantityCell = row.getByTestId("column:quantity");
    await quantityCell.click();
    const quantityInput = quantityCell
      .getByTestId("field:quantity")
      .getByTestId("input");
    await quantityInput.fill(qty.toString());
  }

  // Trigger onchange
  const totalAmount = form.getField("totalAmount");
  const totalAmountTest = await totalAmount.textContent();
  await totalAmount.click();

  // check dirty
  await expect(
    page
      .getByTestId("tab:sale.orders.draft")
      .locator('[data-tab="sale.orders.draft"]'),
  ).toHaveClass(/_dirty_/);

  // save form
  await form.save();

  // final check
  await expect(
    page
      .getByTestId("tab:sale.orders.draft")
      .locator('[data-tab="sale.orders.draft"]'),
  ).not.toHaveClass(/_dirty_/);
  await expect(await totalAmount.textContent()).not.toBe(totalAmountTest);
});
