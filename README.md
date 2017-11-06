#Implemented Things

1. SAX_Parser that extracts Title, ID and Text (body)
2. Single PageProcessor that extracts Categories, Infobox
3. Multi-threading done at XML Parsing. Bunch of 100 pages are processed in a separate thread.
4. Tokenizer - character by character, and regex to extract urls

Created titleIndex, titlePrimary and titleSecondary (3-level Indexing)


Sort on title from linux shell command.

* plainTitle file contents are
<doc-id>:<title>
e.g. 123:India

* titleIndex contents are
<title>
* titlePrimaryIndex contents are [Check it's size]
<doc-id>:<pointer-offset>
* titleSecondary contents are [every nth title]
<doc-id>:<pointer-offset>

