import { expect, Locator, Page } from "@playwright/test";

export enum TOOLBAR_BUTTON {
  SAVE = "item:save",
  NEW = "item:new",
  DELETE = "item:delete",
  REFRESH = "item:refresh",
}

export function getToolBarButton(locator: Locator, type: TOOLBAR_BUTTON) {
  return locator
    .getByTestId("toolbar")
    .getByTestId("common-actions")
    .getByTestId(type)
    .getByRole("button");
}

export async function getMenuBarButton(
  page: Page,
  toolbarId: string,
  buttonId: string,
) {
  const menuBarButton = page
    .getByTestId("toolbar")
    .getByTestId("common-actions")
    .getByTestId(toolbarId)
    .getByRole("button");

  const menuId = await menuBarButton.getAttribute("aria-controls");
  await menuBarButton.click();
  const menu = page.locator(`#${menuId}`);
  await expect(menu).toBeVisible();

  return menu.getByTestId(buttonId);
}
