#let __cc_messages = (
  title: (
    "de": "Sperrvermerk",
    "en": "Confidentiality Clause",
  ),
  subtitle: (
    "de": "Die nachfolgende Arbeit enthält vertrauliche Daten der:",
    "en": "The following thesis contains confidential data from:",
  ),
  clause: (
    "de": "Der Inhalt dieser Arbeit darf weder als Ganzes noch in Auszügen Personen außerhalb des Prüfungsprozesses und des Evaluationsverfahrens zugänglich gemacht werden, sofern keine anderslautende Genehmigung vom Dualen Partner vorliegt.",
    "en": "The content of this thesis must not be made accessible, either in its entirety or in excerpts, to individuals outside the examination process and evaluation procedure, unless there is explicit permission from the partner company.",
  ),
)

#let confidentialityClauseWith(
  lang: "de",
) = [
  #let __cc(name) = __cc_messages.at(name).at(lang)

  #heading(outlined: false, numbering: none, __cc("title"))

  #__cc("subtitle")

  #pad(left: 30pt, top: 0.5cm, bottom: 0.5cm, [
    SAP SE \
    Dietmar-Hopp-Allee 16 \
    69190 Walldorf \
    Deutschland
  ])

  #__cc("clause")
]

#confidentialityClauseWith(lang: "en")
