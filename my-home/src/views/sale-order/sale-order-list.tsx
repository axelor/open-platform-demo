import { useEffect, useMemo, useState } from "react";

import { Badge, Box, Panel, Select } from "@axelor/ui";
import {
  Grid,
  GridProvider,
  type GridColumn,
  type GridRowProps,
} from "@axelor/ui/grid";

import { useFormatDate, useFormatNumber } from "@/hooks/use-format";
import { useGridState } from "@/hooks/use-grid-state";
import { request } from "@/services/client";
import { i18n } from "@/services/i18n";
import { DataRecord } from "@/types";
import { atom, useAtomValue, useSetAtom } from "jotai";

import styles from "./sale-order.module.scss";

declare const __WS_BASE__: string;

const selectedRecordsAtom = atom<DataRecord[]>([]);

interface Currency {
  id: number;
  code: string;
  name: string;
  ["$t:name"]?: string;
  symbol: string;
}

export function SaleOrderList() {
  const formatNumber = useFormatNumber();
  const formatDate = useFormatDate();
  const [state, setState] = useGridState();
  const [records, setRecords] = useState<DataRecord[]>([]);
  const [currencies, setCurrencies] = useState<Currency[]>([]);
  const [selectedCurrency, setSelectedCurrency] = useState<Currency | null>(
    null
  );
  const setSelectedRecords = useSetAtom(selectedRecordsAtom);

  const COLUMNS = useMemo<GridColumn[]>(
    () => [
      { name: "name", title: i18n.get("Name"), type: "string" },
      {
        name: "customer",
        title: i18n.get("Customer"),
        type: "many-to-one",
        formatter: (_: GridColumn, val) => val?.fullName,
      },
      {
        name: "orderDate",
        title: i18n.get("Order date"),
        type: "date",
        formatter: (_: GridColumn, val) => formatDate(val),
      },
      {
        name: "status",
        title: i18n.get("Status"),
        type: "string",
        renderer: RenderStatus,
      },
      {
        name: "currency.symbol",
        title: i18n.get("Currency"),
        type: "string",
        width: 80,
        computed: true,
      },
      {
        name: "totalAmount",
        title: i18n.get("Total amount"),
        type: "decimal",
        aggregate: "sum",
        $css: styles.decimalColumn,
        $headerCss: styles.decimalHeaderColumn,
        formatter: (_: GridColumn, val) =>
          isNaN(val) ? "" : formatNumber(val),
      },
    ],
    [formatDate, formatNumber]
  );

  useEffect(() => {
    (async () => {
      const fetchedCurrencies = await fetchCurrencies();
      setCurrencies(fetchedCurrencies);

      // Set EUR as default if available
      const eurCurrency = fetchedCurrencies.find(
        (currency) => currency.code === "EUR"
      );
      setSelectedCurrency(eurCurrency ?? fetchedCurrencies[0] ?? null);
    })();
  }, []);

  const { orderBy = null } = state;
  useEffect(() => {
    const sortBy = orderBy?.length
      ? orderBy.map((sortColumn) =>
          sortColumn.order === "desc" ? `-${sortColumn.name}` : sortColumn.name
        )
      : null;
    searchSalerOrders({ sortBy, currencyCode: selectedCurrency?.code }).then(
      (_records) => setRecords(_records ?? [])
    );
  }, [orderBy, selectedCurrency]);

  return (
    <GridProvider>
      <Box m={1}>
        <Panel
          header={<span>{i18n.get("Sale Orders")}</span>}
          className={styles.panel}
        >
          {currencies.length > 0 && (
            <Box mb={2}>
              <Select
                placeholder={i18n.get("Select currency")}
                options={currencies}
                value={selectedCurrency}
                onChange={(value) => setSelectedCurrency(value as Currency)}
                optionKey={(currency) => currency.code}
                optionLabel={(currency) =>
                  `${currency.code} - ${currency["$t:name"] ?? currency.name}`
                }
                clearIcon={false}
                toggleIcon={false}
              />
            </Box>
          )}
          <Grid
            allowColumnResize
            allowSelection
            allowCellSelection
            allowCheckboxSelection
            sortType="live"
            state={state}
            setState={setState}
            records={records}
            columns={COLUMNS}
            aggregationType="all"
            onRowSelectionChange={(selectedRows) => {
              setSelectedRecords(
                records.filter((_, index) => selectedRows.includes(index))
              );
            }}
            footerRowRenderer={FooterRowRenderer}
          />
        </Panel>
      </Box>
    </GridProvider>
  );
}

function FooterRowRenderer(props: GridRowProps) {
  const formatNumber = useFormatNumber();
  const selectedRecords = useAtomValue(selectedRecordsAtom);
  const totalAmount = selectedRecords.reduce(
    (acc, row) => acc + Number(row.totalAmount),
    0
  );

  return (
    <Box textAlign="end" pb={2} pt={2} style={{ backgroundColor: "var(--ax-panel-header-bg)" }}>
      <Box as="span" position="absolute" style={{ left: 10 }} fontWeight="bold">
        {i18n.get("Selected Total Amount")}
      </Box>
      <Box as="span" style={{ marginRight: 10 }} fontWeight="bold">
        {formatNumber(totalAmount)}
      </Box>
    </Box>
  );
}

async function fetchCurrencies() {
  const payload = {
    offset: 0,
    translate: true,
    limit: 40,
  };
  const res = await request({
    url: `${__WS_BASE__}ws/rest/com.axelor.sale.db.Currency/search`,
    method: "POST",
    body: payload,
  });
  const result = await res.json();
  return result.data as Currency[];
}

function searchSalerOrders({
  sortBy,
  currencyCode,
}: { sortBy?: null | string[]; currencyCode?: string } = {}) {
  const payload = {
    offset: 0,
    limit: 40,
    fields: [
      "name",
      "customer",
      "orderDate",
      "status",
      "totalAmount",
      "currency.symbol",
      "currency.decimalPlaces",
    ],
    data: currencyCode
      ? { _domain: `self.currency.code = '${currencyCode}'` }
      : { _domain: null },
    sortBy,
  };
  return request({
    url: `${__WS_BASE__}ws/rest/com.axelor.sale.db.Order/search`,
    method: "POST",
    body: payload,
  })
    .then((res) => res.json())
    .then((result) => result.data);
}

function RenderStatus(props: any) {
  const { value, className, style, onClick } = props;
  const getVariant = () => {
    switch (value) {
      case "CLOSED":
        return "success";
      case "OPEN":
        return "warning";
      case "CANCELED":
        return "danger";
      case "DRAFT":
      default:
        return "primary";
    }
  };
  return (
    <Box {...{ className, style, onClick }}>
      <Badge bg={getVariant()}> {i18n.get(toTitleCase(value))} </Badge>
    </Box>
  );
}

function toTitleCase(str: string) {
  return str
    .toLowerCase()
    .split(" ")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}
