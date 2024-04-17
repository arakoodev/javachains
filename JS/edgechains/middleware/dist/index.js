"use strict";var G=Object.create;var A=Object.defineProperty;var $=Object.getOwnPropertyDescriptor;var z=Object.getOwnPropertyNames;var F=Object.getPrototypeOf,W=Object.prototype.hasOwnProperty;var m=(e,a)=>()=>(a||e((a={exports:{}}).exports,a),a.exports),X=(e,a)=>{for(var t in a)A(e,t,{get:a[t],enumerable:!0})},E=(e,a,t,o)=>{if(a&&typeof a=="object"||typeof a=="function")for(let u of z(a))!W.call(e,u)&&u!==t&&A(e,u,{get:()=>a[u],enumerable:!(o=$(a,u))||o.enumerable});return e};var Z=(e,a,t)=>(t=e!=null?G(F(e)):{},E(a||!e||!e.__esModule?A(t,"default",{value:e,enumerable:!0}):t,e)),B=e=>E(A({},"__esModule",{value:!0}),e);var S=m((se,k)=>{"use strict";var H=Object.getOwnPropertySymbols,J=Object.prototype.hasOwnProperty,K=Object.prototype.propertyIsEnumerable;function Q(e){if(e==null)throw new TypeError("Object.assign cannot be called with null or undefined");return Object(e)}function Y(){try{if(!Object.assign)return!1;var e=new String("abc");if(e[5]="de",Object.getOwnPropertyNames(e)[0]==="5")return!1;for(var a={},t=0;t<10;t++)a["_"+String.fromCharCode(t)]=t;var o=Object.getOwnPropertyNames(a).map(function(l){return a[l]});if(o.join("")!=="0123456789")return!1;var u={};return"abcdefghijklmnopqrst".split("").forEach(function(l){u[l]=l}),Object.keys(Object.assign({},u)).join("")==="abcdefghijklmnopqrst"}catch{return!1}}k.exports=Y()?Object.assign:function(e,a){for(var t,o=Q(e),u,l=1;l<arguments.length;l++){t=Object(arguments[l]);for(var c in t)J.call(t,c)&&(o[c]=t[c]);if(H){u=H(t);for(var g=0;g<u.length;g++)K.call(t,u[g])&&(o[u[g]]=t[u[g]])}}return o}});var P=m((ie,C)=>{"use strict";C.exports=ee;C.exports.append=_;var q=/^[!#$%&'*+\-.^_`|~0-9A-Za-z]+$/;function _(e,a){if(typeof e!="string")throw new TypeError("header argument is required");if(!a)throw new TypeError("field argument is required");for(var t=Array.isArray(a)?a:x(String(a)),o=0;o<t.length;o++)if(!q.test(t[o]))throw new TypeError("field argument contains an invalid header name");if(e==="*")return e;var u=e,l=x(e.toLowerCase());if(t.indexOf("*")!==-1||l.indexOf("*")!==-1)return"*";for(var c=0;c<t.length;c++){var g=t[c].toLowerCase();l.indexOf(g)===-1&&(l.push(g),u=u?u+", "+t[c]:t[c])}return u}function x(e){for(var a=0,t=[],o=0,u=0,l=e.length;u<l;u++)switch(e.charCodeAt(u)){case 32:o===a&&(o=a=u+1);break;case 44:t.push(e.substring(o,a)),o=a=u+1;break;default:a=u+1;break}return t.push(e.substring(o,a)),t}function ee(e,a){if(!e||!e.getHeader||!e.setHeader)throw new TypeError("res argument is required");var t=e.getHeader("Vary")||"",o=Array.isArray(t)?t.join(", "):String(t);(t=_(o,a))&&e.setHeader("Vary",t)}});var M=m((oe,T)=>{"use strict";(function(){"use strict";var e=S(),a=P(),t={origin:"*",methods:"GET,HEAD,PUT,PATCH,POST,DELETE",preflightContinue:!1,optionsSuccessStatus:204};function o(n){return typeof n=="string"||n instanceof String}function u(n,r){if(Array.isArray(r)){for(var s=0;s<r.length;++s)if(u(n,r[s]))return!0;return!1}else return o(r)?n===r:r instanceof RegExp?r.test(n):!!r}function l(n,r){var s=r.headers.origin,f=[],i;return!n.origin||n.origin==="*"?f.push([{key:"Access-Control-Allow-Origin",value:"*"}]):o(n.origin)?(f.push([{key:"Access-Control-Allow-Origin",value:n.origin}]),f.push([{key:"Vary",value:"Origin"}])):(i=u(s,n.origin),f.push([{key:"Access-Control-Allow-Origin",value:i?s:!1}]),f.push([{key:"Vary",value:"Origin"}])),f}function c(n){var r=n.methods;return r.join&&(r=n.methods.join(",")),{key:"Access-Control-Allow-Methods",value:r}}function g(n){return n.credentials===!0?{key:"Access-Control-Allow-Credentials",value:"true"}:null}function L(n,r){var s=n.allowedHeaders||n.headers,f=[];return s?s.join&&(s=s.join(",")):(s=r.headers["access-control-request-headers"],f.push([{key:"Vary",value:"Access-Control-Request-Headers"}])),s&&s.length&&f.push([{key:"Access-Control-Allow-Headers",value:s}]),f}function j(n){var r=n.exposedHeaders;if(r)r.join&&(r=r.join(","));else return null;return r&&r.length?{key:"Access-Control-Expose-Headers",value:r}:null}function N(n){var r=(typeof n.maxAge=="number"||n.maxAge)&&n.maxAge.toString();return r&&r.length?{key:"Access-Control-Max-Age",value:r}:null}function b(n,r){for(var s=0,f=n.length;s<f;s++){var i=n[s];i&&(Array.isArray(i)?b(i,r):i.key==="Vary"&&i.value?a(r,i.value):i.value&&r.setHeader(i.key,i.value))}}function I(n,r,s,f){var i=[],h=r.method&&r.method.toUpperCase&&r.method.toUpperCase();h==="OPTIONS"?(i.push(l(n,r)),i.push(g(n,r)),i.push(c(n,r)),i.push(L(n,r)),i.push(N(n,r)),i.push(j(n,r)),b(i,s),n.preflightContinue?f():(s.statusCode=n.optionsSuccessStatus,s.setHeader("Content-Length","0"),s.end())):(i.push(l(n,r)),i.push(g(n,r)),i.push(j(n,r)),b(i,s),f())}function U(n){var r=null;return typeof n=="function"?r=n:r=function(s,f){f(null,n)},function(f,i,h){r(f,function(O,R){if(O)h(O);else{var v=e({},t,R),y=null;v.origin&&typeof v.origin=="function"?y=v.origin:v.origin&&(y=function(w,p){p(null,v.origin)}),y?y(f.headers.origin,function(w,p){w||!p?h(w):(v.origin=p,I(v,f,i,h))}):h()}})}}T.exports=U})()});var V=m(d=>{"use strict";var re=d&&d.__importDefault||function(e){return e&&e.__esModule?e:{default:e}};Object.defineProperty(d,"__esModule",{value:!0});d.Cors=void 0;var ne=re(M());function te(){return ne.default}d.Cors=te});var ae={};X(ae,{Cors:()=>D.Cors});module.exports=B(ae);var D=Z(V());0&&(module.exports={Cors});
/*! Bundled license information:

object-assign/index.js:
  (*
  object-assign
  (c) Sindre Sorhus
  @license MIT
  *)

vary/index.js:
  (*!
   * vary
   * Copyright(c) 2014-2017 Douglas Christopher Wilson
   * MIT Licensed
   *)
*/