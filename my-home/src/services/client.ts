const CSRF_HEADER_NAME = "X-CSRF-Token";
const CSRF_COOKIE_NAME = "CSRF-TOKEN";

type RequestOptions = {
  silent?: boolean;
};

type HttpInterceptorArgs = {
  input: RequestInfo | URL;
  init?: RequestInit;
  options?: RequestOptions;
};

type HttpInterceptor = (
  args: HttpInterceptorArgs,
  next: () => Promise<any>
) => Promise<any>;

const baseURL = process.env.NODE_ENV === "production" ? "./../" : "./";
const interceptors: HttpInterceptor[] = [];

function makeURL(path: string | string[]) {
  const parts = [path].flat();
  const url = parts.join("/");
  if (url.startsWith(baseURL) || url.startsWith("/")) {
    return url;
  }
  return baseURL + url;
}

async function intercept(
  args: HttpInterceptorArgs,
  cb: () => Promise<Response>
) {
  let index = -1;
  const stack: HttpInterceptor[] = [...interceptors];
  const next = async () => {
    const func = stack[++index];
    if (func) {
      return await func(args, next);
    }
    return await cb();
  };
  return await next();
}

async function $request(
  input: RequestInfo | URL,
  init?: RequestInit,
  options?: RequestOptions
) {
  const args: HttpInterceptorArgs = { input, init, options };
  return intercept(args, () => fetch(args.input, args.init));
}

function $use(interceptor: HttpInterceptor) {
  interceptors.push(interceptor);
  return () => {
    const index = interceptors.findIndex((x) => x === interceptor);
    if (index > -1) {
      interceptors.splice(index, 1);
    }
  };
}

const readCookie = (name: string) => {
  const match = document.cookie.match(
    new RegExp("(^|;\\s*)(" + name + ")=([^;]*)")
  );
  return match ? decodeURIComponent(match[3]) : null;
};

// interceptors
$use(async (args, next) => {
  const { init = {} } = args;
  const token = readCookie(CSRF_COOKIE_NAME);
  const headers: Record<string, string> = {
    Accept: "application/json",
  };

  if (token) headers[CSRF_HEADER_NAME] = token;

  args.init = {
    ...init,
    credentials: "include",
    headers: {
      ...init.headers,
      ...headers,
    },
  };
  return next();
});

$use(async (args, next) => {
  const { init = {} } = args;
  const { body } = init;

  // convert plain object or array to JSON string
  if (body && typeof body === "object") {
    init.headers = {
      ...init.headers,
      "Content-Type": "application/json",
    };
    init.body = JSON.stringify(body, (key, value) => {
      //XXX: exclude dymmy fields, should be removed in next major release
      return key.startsWith("$") ? undefined : value;
    });
  }

  return next();
});

type RequestArgs = {
  url: string;
  method?: "GET" | "PUT" | "POST" | "PATCH" | "DELETE" | "HEAD" | "OPTIONS";
  headers?: HeadersInit;
  signal?: AbortSignal;
  body?: any;
  options?: RequestOptions;
};

export async function request(args: RequestArgs): Promise<Response> {
  const { url, method, headers, body, options } = args;
  const input = makeURL(url);
  const init = {
    method,
    headers,
    body,
  };
  return $request(input, init, options);
}
