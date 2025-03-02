<#assign emailSubject = msg("identityProviderLinkSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout, emailSubject=emailSubject>
${kcSanitize(msg("identityProviderLinkBodyHtml", identityProviderDisplayName, realmName, identityProviderContext.username, link, linkExpiration, linkExpirationFormatter(linkExpiration)))?no_esc}
</@layout.emailLayout>
