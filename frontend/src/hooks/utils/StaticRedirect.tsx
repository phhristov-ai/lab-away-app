import { useEffect } from "react";

const StaticRedirect = () => {
  useEffect(() => {
    // Forces the browser to reload the current URL
    globalThis.location.assign(globalThis.location.href);
  }, []);

  return null;
};

export default StaticRedirect;