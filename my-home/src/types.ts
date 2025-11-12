import { ThemeOptions } from "@axelor/ui/core/styles/theme/types";
import { SvgIconProps } from "@axelor/ui/icons/svg-icon";
import { ButtonProps } from "@axelor/ui";
import { ComponentProps } from "react";

export interface ClientInfo {
  name: string;
  icon?: string;
  title?: string;
}

export type SignInButtonType = {
  type?: "button" | "link";
  title?: string;
  variant?: ButtonProps["variant"];
  icon?: SvgIconProps["as"];
  link?: ComponentProps<"a">["href"];
  order?: number;
};

export type AppData = {
  info: {
    application: {
      name?: string;
      author?: string;
      description?: string;
      copyright?: string;
      theme?: string;
      lang?: string;
      version?: string;
      home?: string;
      help?: string;
      mode?: string;
      aopVersion?: string;
      aopBuildDate?: string;
      aopGitHash?: string;
      pollingInterval?: number;
      swaggerUI?: {
        enabled?: boolean;
        allowTryItOut?: boolean;
      };
      signIn?: {
        title?: string;
        footer?: string;
        fields?: {
          [key in "username" | "password" | "tenant"]: {
            showTitle?: boolean;
            title?: string;
            placeholder?: string;
            icon?: string;
          };
        };
        buttons?: {
          [key in "submit" | string]: SignInButtonType;
        };
      };
      resetPasswordEnabled?: boolean;
    };
    authentication?: {
      callbackUrl?: string;
      clients?: ClientInfo[];
      defaultClient?: string;
      exclusive?: boolean;
      currentClient?: string;
      tenants?: Record<string, string>;
      tenant?: string;
    };
    user?: {
      id: number;
      login: string;
      name: string;
      nameField?: string;
      lang?: string | null;
      image?: string | null;
      action?: string | null;
      singleTab?: boolean;
      noHelp?: boolean;
      theme?: string | null;
      group?: string | null;
      navigator?: string | null;
      technical?: boolean;
      viewCustomizationPermission?: number;
      canViewCollaboration?: boolean;
    };
    view?: {
      singleTab?: boolean;
      maxTabs?: number;
      form?: {
        checkVersion?: boolean;
      };
      grid?: {
        selection?: "checkbox" | "none";
      };
      advancedSearch?: {
        exportFull?: boolean;
        share?: boolean;
      };
      allowCustomization?: boolean;
      collaboration?: {
        enabled?: boolean;
      };
    };
    api?: {
      pagination?: {
        maxPerPage?: number;
        defaultPerPage?: number;
      };
    };
    data?: {
      upload?: {
        maxSize?: number;
      };
    };
    features?: {
      dmsSpreadsheet?: boolean;
      studio?: boolean;
    };
    route?: {
      path: string;
      state: Record<string, unknown>;
    };
  } | null;
  theme: string;
  options: ThemeOptions; //
  dir?: string;
  lang?: string;
};

export type DataRecord = {
  id?: number | null;
  cid?: number | null;
  version?: number | null;
  $version?: number | null;
  selected?: boolean;
  [k: string]: any;
};
