<#assign emailSubject = msg("emailVerificationSubjectCode")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("emailVerificationBodyCodeHtml",code))?no_esc}
</@layout.emailLayout>
