<#assign emailSubject = msg("eventRemoveTotpSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("eventRemoveTotpBodyHtml",event.date, event.ipAddress))?no_esc}
</@layout.emailLayout>
