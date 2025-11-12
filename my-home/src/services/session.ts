import { AppData } from "@/types";
import { createContext, createElement, ReactNode, useContext } from "react";

export type SessionInfo = AppData["info"];

const SessionContext = createContext<SessionInfo | null>(null);

export function SessionProvider({
  children,
  info,
}: {
  children: ReactNode;
  info: SessionInfo;
}) {
  return createElement(SessionContext.Provider, {
    children,
    value: info,
  });
}

export function useSession() {
  return useContext(SessionContext);
}

export function useSessionUser() {
  return useSession()?.user;
}
