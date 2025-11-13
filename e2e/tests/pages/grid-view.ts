import { Page, Locator, expect } from "@playwright/test";
import { getToolBarButton, TOOLBAR_BUTTON } from "./utils";

export class GridView {
  readonly page: Page;
  readonly gridView: Locator;

  constructor(private _page: Page) {
    this.page = _page;
    this.gridView = _page.getByTestId("view:grid");
  }

  async save() {
    await getToolBarButton(this.gridView, TOOLBAR_BUTTON.SAVE).click();
  }

  async delete() {
    await getToolBarButton(this.gridView, TOOLBAR_BUTTON.DELETE).click();
  }

  async newRecord() {
    await getToolBarButton(this.gridView, TOOLBAR_BUTTON.NEW).click();
  }

  async openAdvanceSearch() {
    await this.gridView
      .getByTestId("advance-search-input")
      .getByTestId("icon:arrow_drop_down")
      .click();
    const advanceSearch = this.page.getByRole("dialog").last();
    await expect(advanceSearch).toBeVisible();
    return advanceSearch;
  }
}
