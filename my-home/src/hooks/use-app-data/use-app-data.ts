import { useEffect, useState } from "react";

import { AppData } from "@/types";
import { getAxelorScope } from "@/services/axelor";
import { request } from "@/services/client";

declare const __WS_BASE__: string;

export async function fetchUserInfoData() {
  try {
    return request({
      url: `${__WS_BASE__}ws/public/app/info`,
      method: "GET",
    }).then((res) => res.json());
  } catch (err) {
    // handle err
    return null;
  }
}

export function useAppData() {
  const [appData, setAppData] = useState<AppData | null>(null);

  useEffect(() => {
    const scope = getAxelorScope();
    if (scope?.getAppData) {
      scope.getAppData().then(setAppData);
    } else {
      console.warn("Axelor scope not found");
      // local development case
      fetchUserInfoData().then((info) => {
        setAppData(
          info
            ? {
                theme: info.user.theme || "light",
                options: {},
                info,
              }
            : null
        );
      });
    }

    if (scope?.onAppDataChanged) {
      return scope.onAppDataChanged(setAppData);
    }
  }, []);

  return appData;
}
