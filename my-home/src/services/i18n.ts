import { getAxelorScope } from "./axelor";

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace i18n {
  export function get(text: string, ...args: any[]): string {
    const scope = getAxelorScope()?.i18n;
    if (scope?.get) {
      return scope.get(text, ...args);
    }
    return text;
  }
}
