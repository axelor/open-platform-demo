import { Box, CircularProgress, ThemeProvider } from "@axelor/ui/core";

import { SaleOrderList } from "./views/sale-order";
import { useAppData } from "./hooks/use-app-data";
import { i18n } from "./services/i18n";
import { SessionProvider } from "./services/session";

function Loader() {
  return (
    <Box
      w={100}
      h={100}
      position="fixed"
      d="flex"
      flexDirection={"column"}
      alignItems={"center"}
      justifyContent={"center"}
    >
      <Box>
        <CircularProgress size={25} indeterminate />
      </Box>
      <Box>{i18n.get("Loading")}...</Box>
    </Box>
  );
}

function App() {
  const appData = useAppData();

  if (!appData) {
    return <Loader />;
  }

  const { dir, theme, options } = appData;

  return (
    <ThemeProvider dir={dir} theme={theme} options={options}>
      <SessionProvider info={appData.info}>
        <SaleOrderList />
      </SessionProvider>
    </ThemeProvider>
  );
}

export default App;
