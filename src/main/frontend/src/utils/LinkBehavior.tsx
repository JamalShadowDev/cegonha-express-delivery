import React from "react";
import {
  Link as RouterLink,
  type LinkProps as RouterLinkProps,
} from "react-router-dom";

interface CustomLinkProps extends Omit<RouterLinkProps, "to"> {
  href: RouterLinkProps["to"];
  target?: string; // Adiciona 'target'
  rel?: string; // Adiciona 'rel'
}

const LinkBehavior = React.forwardRef<HTMLAnchorElement, CustomLinkProps>(
  (props, ref) => {
    const { href, target, rel, ...other } = props;
    return (
      <RouterLink ref={ref} to={href} target={target} rel={rel} {...other} />
    );
  },
);

LinkBehavior.displayName = "LinkBehavior";

export default LinkBehavior;
