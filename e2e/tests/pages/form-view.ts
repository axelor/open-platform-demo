import { Locator, Page } from "@playwright/test";
import { getMenuBarButton, getToolBarButton, TOOLBAR_BUTTON } from "./utils";

export class FormView {
  readonly page: Page;
  readonly formView: Locator;

  constructor(private _page: Page) {
    this.page = _page;
    this.formView = _page.getByTestId("view:form");
  }

  async save() {
    await getToolBarButton(this.formView, TOOLBAR_BUTTON.SAVE).click();
  }

  async delete() {
    const button = await getMenuBarButton(
      this.page,
      "item:more",
      TOOLBAR_BUTTON.DELETE,
    );
    await button.click();
  }

  async newRecord() {
    await getToolBarButton(this.formView, TOOLBAR_BUTTON.NEW).click();
  }

  getField(name: string): Locator {
    return this.formView.getByTestId("field:" + name);
  }
}
