// This is a workaround solution to display the month name in the current language.
// This should be used temporarily, until Typst becomes capable of handling month names in different languages natively.
#import "../utils.typ": program-specific

#let month_names = (
  "de": (
    "Januar",
    "Februar",
    "März",
    "April",
    "Mai",
    "Juni",
    "Juli",
    "August",
    "September",
    "Oktober",
    "November",
    "Dezember",
  ),
  "en": (
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ),
)

#let __doo_program_clause_message = (
  "de": (
    "dhbw": [gemäß § 5 der "Studien- und Prüfungsordnung DHBW Technik" vom 29. September 2017 selbstständig verfasst und keine anderen als die angegebenen Quellen und Hilfsmittel benutzt habe. Die Arbeit wurde bisher keiner anderen Prüfungsbehörde vorgelegt und auch nicht veröffentlicht.],
    "ihk": [selbstständig verfasst und keine anderen als die angegebenen Quellen und Hilfsmittel benutzt habe. Die Arbeit wurde bisher keiner anderen Prüfungsbehörde vorgelegt und auch nicht veröffentlicht.],
  ),
  "en": (
    "dhbw": "in accordance with § 5 of the \"Studien- und Prüfungsordnung DHBW Technik\" dated September 29, 2017, and have not used any sources or aids other than those specified. The work has not been submitted to any other examining authority before and has not been published.",
    "ihk": "independently and have not used any sources or aids other than those specified. The work has not been submitted to any other examining authority before and has not been published.",
  ),
)

#let __doo_messages_array(singular) = (
  (
    "de": "Eidesstattliche Erklärung",
    "en": "Declaration of Originality",
  ),
  (
    "de": if singular {
      "Ich versichere hiermit, dass ich meine #type mit dem Thema:"
    } else {
      "Wir versichern hiermit, dass wir unsere #type mit dem Thema:"
    },
    "en": if singular {
      "I hereby declare that I have independently written my #type with the topic:"
    } else {
      "We hereby declare that we have independently written our #type with the topic:"
    },
  ),
  (
    "de": if singular {
      "Ich versichere zudem, dass die eingereichte elektronische Fassung mit der gedruckten Fassung übereinstimmt."
    } else {
      "Wir versichern zudem, dass die eingereichte elektronische Fassung mit der gedruckten Fassung übereinstimmt."
    },
    "en": if singular {
      "Furthermore, I confirm that the submitted electronic version corresponds to the printed version."
    } else {
      "Furthermore, we confirm that the submitted electronic version corresponds to the printed version."
    },
  ),
  (
    "de": "gez.",
    "en": "Signed",
  ),
)

#let declarationOfOriginalityWith(
  title_long: none,
  is_digital: none,
  authors: none,
  signature_place: "Karlsruhe",
  type: "Praxisarbeit",
  lang: "de",
  program: "dhbw",
) = [
  #let one_author = authors.len() == 1

  #let __doo_messages(idx) = (
    return __doo_messages_array(one_author).at(idx).at(lang),
  )

  #let author_reversed = (
    authors.map(author => [#author.lastname, #author.firstname]).join("; ")
  )

  #heading(
    outlined: false,
    numbering: none,
    __doo_messages(0),
  )
  #__doo_messages(1).replace("#type", type)
  #v(0.3cm)
  #pad(left: 30pt, text(style: "italic", baseline: -4pt, title_long))
  #program-specific(
    program,
    __doo_program_clause_message.at(lang),
  )
  #v(0.3cm)
  #__doo_messages(2)
  #v(0.2cm)

  // show sign field
  #let current_month_number = datetime.today().month()
  #let current_month = month_names.at(lang).at(current_month_number - 1)
  #signature_place, #datetime.today().display("[day]. current_month [year]").replace("current_month", current_month)

  // on digital prints, we don't need a signature line
  #if is_digital [
    #linebreak()
    #__doo_messages(3): #author_reversed
  ] else [
    #v(0.5cm)
    #line(length: 6cm)
    #author_reversed
  ]
]
