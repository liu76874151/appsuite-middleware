---
title: Mail Text Previews
icon: fa-align-justify
tags: Mail, Configuration, Installation
---

# Motivation

Starting with v7.10.0, the Open-Xchange Server supports the delivery of text previews for E-Mails, which are shown in list views to the user.

# Prerequisites

In order to deliver text previews for E-Mails held in an IMAP mail back-end, the package `open-xchange-imap` needs to be installed and the IMAP server is supposed to advertise the `"SNIPPET=FUZZY"` capability.

# Requesting mail text previews via HTTP-API

For requesting text previews, the Open-Xchange Server introduced two columns for the `/mail` HTTP-API end-point:

* `662` (`TEXT_PREVIEW_IF_AVAILABLE`) Requests a message's text preview only if immediately available by IMAP server. If not available, `null` is returned
* `663` (`TEXT_PREVIEW`) Requests a message's text preview, waiting if necessary for a text preview to be generated by IMAP server

An empty string is advertised for those columns in case referenced E-Mail has no text content.

In case of IMAP, when the `662` (`TEXT_PREVIEW_IF_AVAILABLE`) column is given, the `"SNIPPET (LAZY=FUZZY)"` [fetch item](https://tools.ietf.org/html/rfc3501.html#section-6.4.5) is used to query mail text previews (if available). 
For `663` (`TEXT_PREVIEW`), the `"SNIPPET (FUZZY)"` fetch item is issued.

## App Suite UI

To enable mail text previews in OX AppSuite UI, the `io.ox/mail//features/textPreview` feature toggle is supposed to be enabled.

## IMAP specification

Please see [this Internet draft](https://tools.ietf.org/html/draft-slusarz-imap-fetch-snippet-00) for the formal IMAP specification, which the Open-Xchange Server is using.