import React from "react";
import { produce } from "immer";
import { type GridState, type GridStateHandler } from "@axelor/ui/grid";

export function useGridState(
  initState?: Partial<GridState>
): [GridState, (state: GridState | GridStateHandler) => void] {
  const [state, setState] = React.useState<GridState>({
    columns: [],
    rows: [],
    ...initState,
  });

  const setMutableState = React.useCallback(
    (_state: GridState | GridStateHandler) => setState(produce(_state as any) as any),
    [setState]
  );

  return [state, setMutableState];
}
