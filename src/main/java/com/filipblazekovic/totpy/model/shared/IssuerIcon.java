package com.filipblazekovic.totpy.model.shared;

import java.util.Arrays;

public enum IssuerIcon {

  ABOUTME,
  ACADEMIA,
  AIRBNB,
  ALCHEMY,
  AMAZONAWS,
  AMAZON,
  APPLE,
  ATLASSIAN,
  BABEL,
  BADOO,
  BITBUCKET,
  BITCOIN,
  CLOUDFLARE,
  CODECADEMY,
  COINBASE,
  CONFLUENCE,
  COUCHBASE,
  CPANEL,
  CRUNCHBASE,
  DEVIANTART,
  DOCKER,
  DPD,
  DUCKDUCKGO,
  EBAY,
  EDX,
  EPICGAMES,
  ETHEREUM,
  ETSY,
  EVERNOTE,
  FACEBOOK,
  FREELANCER,
  GITHUB,
  GITLAB,
  GLASSDOOR,
  GLOVO,
  GMAIL,
  GOFUNDME,
  GOLDMANSACHS,
  GOOGLE,
  GRAFANA,
  GRAMMARLY,
  GRAYLOG,
  INDEED,
  INSTAGRAM,
  INTELLIJ,
  ITUNES,
  JENKINS,
  JIRA,
  KICKSTARTER,
  KUBERNETES,
  LINKEDIN,
  LINUX,
  MEDIUM,
  META,
  MICROSOFT,
  MONEYGRAM,
  MOZILLA,
  MYSPACE,
  NETFLIX,
  NGROK,
  NODEJS,
  OKCUPID,
  ONLYFANS,
  OPENAI,
  OPENVPN,
  PAYONEER,
  PAYPAL,
  PINTEREST,
  POSTMAN,
  PROMETHEUS,
  PROTONMAIL,
  PROTON,
  RASPBERRYPI,
  REDDIT,
  RESEARCHGATE,
  REVOLUT,
  RIOTGAMES,
  ROBINHOOD,
  SAMSUNG,
  SHOPIFY,
  SIGNAL,
  SKILLSHARE,
  SKYPE,
  SLACK,
  SNAPCHAT,
  SOURCEFORGE,
  SPOTIFY,
  SQUARESPACE,
  STACKOVERFLOW,
  STRIPE,
  TELEGRAM,
  TESLA,
  TINDER,
  TOPTAL,
  TORPROJECT,
  TRIPADVISOR,
  TRUSTPILOT,
  TWITCH,
  TWITTER,
  UBER,
  UPWORK,
  VK,
  WELLSFARGO,
  WHATSAPP,
  WIKIPEDIA,
  WIRE,
  WIREGUARD,
  WISE,
  WOLFRAM,
  X,
  XERO,
  XIAOMI,
  ZILLOW,
  ZOOM,
  UNKNOWN;

  public static IssuerIcon from(String name) {
    if (name == null || name.trim().isEmpty()) {
      return IssuerIcon.UNKNOWN;
    }
    final String searchPattern = name.toUpperCase().trim();
    return Arrays
        .stream(IssuerIcon.values())
        .filter(icon -> {
          if (icon.name().length() <= 4) {
            return searchPattern.equals(icon.name());
          }
          return searchPattern.contains(icon.name());
        })
        .findFirst()
        .orElse(IssuerIcon.UNKNOWN);
  }

}