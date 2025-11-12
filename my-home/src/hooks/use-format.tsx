import { useAppData } from "@/hooks/use-app-data";
import { useCallback } from "react";

export function useFormatNumber() {
  const appData = useAppData();
  const { lang } = appData ?? {};

  return useCallback(
    (number: number | string, decimalPlaces: number = 2) =>
      new Intl.NumberFormat(lang ?? navigator.language, {
        minimumFractionDigits: decimalPlaces,
        maximumFractionDigits: decimalPlaces,
      }).format(Number(number)),
    [lang]
  );
}

export function useFormatDate() {
  const appData = useAppData();
  const { lang } = appData ?? {};

  return useCallback(
    (date: string) =>
      new Intl.DateTimeFormat(lang ?? navigator.language, {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      }).format(new Date(date)),
    [lang]
  );
}
