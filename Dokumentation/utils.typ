// LTeX: enabled=false

/// codefigure creates a figure with a caption.
/// figures created using this function will also appear in the
/// source-code table of contents.
///
/// * The `caption` parameter is optional and can be used to add a caption to the figure.
/// * The `reference` parameter is optional and can be used to reference the figure
/// * The `v-space` parameter is optional and can be used to add vertical space after the figure.
#let codefigure(body, caption: none, reference: none) = {
  [
    #figure(
      body,
      caption: caption,
      outlined: true,
      supplement: [Code],
    )
    #if reference != none {
      label(reference)
    }
  ]
}

/// codefigurefile is an alias to `codefigure` that reads the content of a file and uses it as the body.
/// The `file` parameter is required and should be the path to the file.
/// If you don't specify the `lang` parameter, the language will be extracted from the file extension.
///
/// * The `caption` parameter is optional and can be used to add a caption to the figure.
///
/// * The `reference` parameter is optional and can be used to reference the figure
#let codefigurefile(
  file,
  caption: none,
  reference: none,
  lang: none,
) = {
  // extract language from file name if no lang was specified
  if lang == none {
    if file.contains(".") {
      lang = file.split(".").last()
    }
  }
  codefigure(
    raw(read(file), lang: lang, block: true),
    caption: caption,
    reference: reference,
  )
}

/// `hr` creates a horizontal line.
#let hr = line(length: 100%, stroke: (paint: gray))

/// `program-specific` retrieves a value from a dictionary based on the provided program key.
#let program-specific(program, content) = if program not in content {
  panic(
    "Unknown program type "
      + program
      + "! Allowed values are:"
      + content.keys().join(", "),
  )
} else {
  content.at(program)
}
