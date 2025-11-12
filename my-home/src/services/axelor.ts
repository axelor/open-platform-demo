
export function getAxelorScope() {
  return (window as any).top?.parent?.axelor;
}
