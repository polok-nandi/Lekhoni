package com.example.model

import java.util.UUID

data class TemplateItem(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val iconName: String,
    val documentFactory: () -> LekhoniDocument
)

object StarterTemplates {
    val templates: List<TemplateItem> = listOf(
        TemplateItem(
            id = "blank",
            nameEn = "Blank Document",
            nameBn = "খালি নথি",
            descriptionEn = "Start fresh with a clean page",
            descriptionBn = "একটি নতুন পরিষ্কার পৃষ্ঠা দিয়ে শুরু করুন",
            iconName = "description",
            documentFactory = {
                LekhoniDocument(
                    title = "নতুন নথি",
                    blocks = listOf(
                        DocumentBlock(type = BlockType.PARAGRAPH, text = "")
                    )
                )
            }
        ),
        TemplateItem(
            id = "official_letter",
            nameEn = "Bengali Official Letter",
            nameBn = "দাপ্তরিক আবেদনপত্র",
            descriptionEn = "Standard formal Bengali official letter format",
            descriptionBn = "যথাযথ আনুষ্ঠানিক বাংলা দাপ্তরিক চিঠির কাঠামো",
            iconName = "mail",
            documentFactory = {
                LekhoniDocument(
                    title = "দাপ্তরিক আবেদনপত্র",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "তারিখ: ২৪ অক্টোবর ২০২৬",
                            formatting = BlockFormatting(alignment = TextAlignOption.RIGHT)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "বরাবর,\nযুগ্ম সচিব\nতথ্য ও যোগাযোগ প্রযুক্তি বিভাগ\nবাংলাদেশ সচিবালয়, ঢাকা।",
                            formatting = BlockFormatting(lineSpacingMultiplier = 1.3f)
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "বিষয়: মুক্ত-উৎস বাংলা ডকুমেন্ট এডিটর 'লেখনী' প্রচলন প্রসঙ্গে।",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 18f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "মহোদয়,\nযথাবিহিত সম্মান প্রদর্শনপূর্বক বিনীত নিবেদন এই যে, আমাদের দৈনন্দিন প্রশাসনিক কাজে সুলভ ও নিরাপদ বাংলা নথিপত্র তৈরির প্রয়োজনীয়তা অনস্বীকার্য। 'লেখনী' উন্মুক্ত ও বাংলা-বান্ধব অফিস সফটওয়্যার হিসেবে অত্যন্ত সহায়ক ভূমিকা পালন করতে সক্ষম।",
                            formatting = BlockFormatting(lineSpacingMultiplier = 1.4f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "অতএব, মহোদয়ের নিকট বিনীত প্রার্থনা, উক্ত মুক্ত-উৎস সফটওয়্যারটি জাতীয় পর্যায়ে ব্যবহারের জন্য সদয় বিবেচনা ও প্রয়োজনীয় পদক্ষেপ গ্রহণে বাধিত করবেন।",
                            formatting = BlockFormatting(lineSpacingMultiplier = 1.4f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "বিনীত,\nপলক নন্দী\nউদ্যোক্তা ও প্রধান সফটওয়্যার স্থপতি\nলেখনী প্রকল্প",
                            formatting = BlockFormatting(bold = true, alignment = TextAlignOption.RIGHT)
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "job_application",
            nameEn = "Job Application",
            nameBn = "চাকরির আবেদন",
            descriptionEn = "Professional job application and cover letter",
            descriptionBn = "পেশাদার চাকরির আবেদনপত্র ও কভার লেটার",
            iconName = "work",
            documentFactory = {
                LekhoniDocument(
                    title = "চাকরির আবেদনপত্র - সফটওয়্যার ইঞ্জিনিয়ার",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "তারিখ: ২৪ অক্টোবর ২০২৬",
                            formatting = BlockFormatting(alignment = TextAlignOption.RIGHT)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "বরাবর,\nমানবসম্পদ বিভাগ প্রধান\nপ্রযুক্তি উদ্ভাবন লিমিটেড\nগুলশান-২, ঢাকা-১২১২।"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "বিষয়: সিনিয়র অ্যান্ড্রয়েড সফটওয়্যার ইঞ্জিনিয়ার পদের জন্য আবেদন।",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 18f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "মহোদয়,\nআপনার সম্প্রতি প্রকাশিত নিয়োগ বিজ্ঞপ্তির মাধ্যমে অবগত হলাম যে আপনার স্বনামধন্য প্রতিষ্ঠানে সিনিয়র অ্যান্ড্রয়েড সফটওয়্যার ইঞ্জিনিয়ার পদে নিয়োগ প্রদান করা হবে। আমি উক্ত পদের একজন যোগ্য ও উৎসাহী প্রার্থী হিসেবে আমার আবেদনপত্র পেশ করছি।",
                            formatting = BlockFormatting(lineSpacingMultiplier = 1.4f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "আমার বিগত ৬ বছরের অ্যান্ড্রয়েড ও জেটপ্যাক কম্পোজ উন্নয়নের অভিজ্ঞতা এবং 'লেখনী' ডকুমেন্ট সম্পাদকের মতো বাস্তব প্রকল্প তৈরির অভিজ্ঞতা আপনার প্রতিষ্ঠানের অগ্রগতিতে ভূমিকা রাখতে সক্ষম হবে বলে আমি দৃঢ়ভাবে বিশ্বাস করি।"
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "বিনীত নিবেদক,\nআবেদনকারী\nযোগাযোগ: +৮৮০১৭১০০০০০০০ | email@example.com"
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "resume_cv",
            nameEn = "CV or Résumé",
            nameBn = "জীবনবৃত্তান্ত (CV)",
            descriptionEn = "Clean, modern curriculum vitae format",
            descriptionBn = "আধুনিক ও পরিপাটি জীবনবৃত্তান্ত ফরম্যাট",
            iconName = "badge",
            documentFactory = {
                LekhoniDocument(
                    title = "জীবনবৃত্তান্ত - পলক নন্দী",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "পলক নন্দী (Polok Nandi)",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 24f, alignment = TextAlignOption.CENTER)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "সফটওয়্যার আর্কিটেক্ট ও মুক্ত-উৎস গবেষক | ঢাকা, বাংলাদেশ\nইমেইল: nandipolok@gmail.com | গিটহাব: github.com/poloknandi",
                            formatting = BlockFormatting(alignment = TextAlignOption.CENTER, fontSizeSp = 13f)
                        ),
                        DocumentBlock(type = BlockType.DIVIDER),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "পেশাগত দক্ষতা ও পরিচিতি",
                            formatting = BlockFormatting(bold = true)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "অ্যান্ড্রয়েড আর্কিটেকচার, জেটপ্যাক কম্পোজ, বাংলা ইউনিকোড টাইপোগ্রাফি ও ওপেন-সোর্স সফটওয়্যার তৈরিতে দক্ষ ফুল-স্ট্যাক বিশেষজ্ঞ।"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "মূল প্রজেক্টসমূহ",
                            formatting = BlockFormatting(bold = true)
                        ),
                        DocumentBlock(
                            type = BlockType.BULLET_LIST,
                            text = "লেখনী (Lekhoni): ওপেন-সোর্স বাংলা ডকুমেন্ট এডিটর ও অফিস স্যুট।"
                        ),
                        DocumentBlock(
                            type = BlockType.BULLET_LIST,
                            text = "বাংলা টাইপোগ্রাফি রূপান্তর ইঞ্জিন: নির্ভুল যুক্তাক্ষর ও কার-ফলা সহায়ক রেন্ডারিং ফ্রেমওয়ার্ক।"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "শিক্ষাগত যোগ্যতা",
                            formatting = BlockFormatting(bold = true)
                        ),
                        DocumentBlock(
                            type = BlockType.TABLE,
                            tableData = TableData(
                                rows = listOf(
                                    listOf("ডিগ্রি", "প্রতিষ্ঠান", "বছর", "ফলাফল"),
                                    listOf("বিএসসি ইন সিএসই", "ঢাকা বিশ্ববিদ্যালয়", "২০২৩", "৩.৮৫"),
                                    listOf("এইচএসসি (বিজ্ঞান)", "নটর ডেম কলেজ", "২০১৯", "৫.০০")
                                ),
                                colCount = 4
                            )
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "academic_assignment",
            nameEn = "Academic Assignment",
            nameBn = "একাডেমিক অ্যাসাইনমেন্ট",
            descriptionEn = "University or college assignment cover & content",
            descriptionBn = "বিশ্ববিদ্যালয় ও কলেজের অ্যাসাইনমেন্ট নথি",
            iconName = "school",
            documentFactory = {
                LekhoniDocument(
                    title = "একাডেমিক অ্যাসাইনমেন্ট - কম্পিউটার বিজ্ঞান",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "কম্পিউটার বিজ্ঞান ও প্রকৌশল বিভাগ",
                            formatting = BlockFormatting(bold = true, alignment = TextAlignOption.CENTER, fontSizeSp = 22f)
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "কোর্স: ডাটা স্ট্রাকচার ও অ্যালগরিদম (CSE-201)",
                            formatting = BlockFormatting(alignment = TextAlignOption.CENTER, fontSizeSp = 18f)
                        ),
                        DocumentBlock(type = BlockType.DIVIDER),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "অ্যাসাইনমেন্ট শিরোনাম: ইউনিকোড বাংলা টেক্সট প্রসেসিং অ্যালগরিদম বিশ্লেষণ",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 16f)
                        ),
                        DocumentBlock(
                            type = BlockType.BLOCKQUOTE,
                            text = "তত্ত্বাবধানে: ড. রফিকুল ইসলাম, অধ্যাপক\nউপস্থাপনায়: রোল-৪৫, শিক্ষাবর্ষ: ২০২৩-২৪"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "১. ভূমিকা ও পটভূমি"
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "কম্পিউটিং ব্যবস্থায় বাংলা ভাষার সঠিক রেন্ডারিং এবং সার্চিং একটি গুরুত্বপূর্ণ চ্যালেঞ্জ। যুক্তাক্ষর ভাঙা ও সন্ধি প্রক্রিয়াকরণে আধুনিক ডেটা স্ট্রাকচারের প্রয়োগ অপরিহার্য।"
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "research_paper",
            nameEn = "Research Paper",
            nameBn = "গবেষণা পত্র",
            descriptionEn = "Academic paper with abstract, sections and references",
            descriptionBn = "গবেষণাপত্রের সারসংক্ষেপ, বিভাগ ও তথ্যসূত্র বিন্যাস",
            iconName = "menu_book",
            documentFactory = {
                LekhoniDocument(
                    title = "গবেষণা পত্র - আধুনিক বাংলা কম্পিউটিং",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "আধুনিক মোবাইল প্ল্যাটফর্মে বাংলা লিগ্যাচার ও কমপ্লেক্স টেক্সট লেআউট মডেল",
                            formatting = BlockFormatting(bold = true, alignment = TextAlignOption.CENTER, fontSizeSp = 22f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "পলক নন্দী | গবেষণা ও উদ্ভাবন কেন্দ্র",
                            formatting = BlockFormatting(alignment = TextAlignOption.CENTER, italic = true)
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "সারসংক্ষেপ (Abstract)",
                            formatting = BlockFormatting(bold = true)
                        ),
                        DocumentBlock(
                            type = BlockType.BLOCKQUOTE,
                            text = "এই গবেষণায় মোবাইল অ্যাপ্লিকেশনে বাংলা যুক্তাক্ষরের সঠিক অবস্থান, কার ও ফলার ভেক্টর রূপান্তর এবং দ্রুতগতির অফলাইন পিডিএফ জেনারেশন কৌশল আলোচনা করা হয়েছে।"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "১. মূল সমস্যা বিশ্লেষণ (Introduction)"
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "বাংলা ভাষার ব্যাকরণগত নিয়ম অনুযায়ী কার ও ফলা স্বরবর্ণ ও ব্যঞ্জনবর্ণের অবস্থানের উপর নির্ভর করে। মুক্ত-উৎস 'লেখনী' ইঞ্জিন এই সমস্যাগুলো সমাধান করে।"
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "business_report",
            nameEn = "Business Report",
            nameBn = "ব্যবসায়িক প্রতিবেদন",
            descriptionEn = "Executive summary, metric charts, and findings",
            descriptionBn = "নির্বাহী সারসংক্ষেপ, মেট্রিক চার্ট ও সুপারিশমালা",
            iconName = "analytics",
            documentFactory = {
                LekhoniDocument(
                    title = "ত্রৈমাসিক ব্যবসায়িক প্রতিবেদন ২০২৬",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "ত্রৈমাসিক ব্যবসায়িক মূল্যায়ন প্রতিবেদন",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 22f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "প্রতিবেদনকাল: জুলাই - সেপ্টেম্বর ২০২৬ | প্রস্তুতকারী: গবেষণা দল"
                        ),
                        DocumentBlock(type = BlockType.DIVIDER),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "কর্মক্ষমতা নির্দেশক চার্ট",
                            formatting = BlockFormatting(bold = true)
                        ),
                        DocumentBlock(
                            type = BlockType.CHART,
                            chartData = ChartData(
                                chartType = ChartType.BAR,
                                title = "বিভাগভিত্তিক রাজস্ব বৃদ্ধি (লক্ষ টাকা)",
                                labels = listOf("ঢাকা", "চট্টগ্রাম", "সিলেট", "রাজশাহী"),
                                values = listOf(52f, 78f, 44f, 63f)
                            )
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "সুপারিশ ও ভবিষ্যৎ পরিকল্পনা"
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "আমাদের প্রযুক্তি ও গ্রাহক সেবার বিস্তৃতির ফলে রাজস্বে ২০% প্রবৃদ্ধি অর্জিত হয়েছে।"
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "meeting_minutes",
            nameEn = "Meeting Minutes",
            nameBn = "সভার কার্যবিবরণী",
            descriptionEn = "Attendees, agenda items, and action points",
            descriptionBn = "উপস্থিত সদস্য, আলোচ্যসূচি ও গৃহীত সিদ্ধান্তসমূহ",
            iconName = "groups",
            documentFactory = {
                LekhoniDocument(
                    title = "পরিচালনা পর্ষদের মাসিক সভার কার্যবিবরণী",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "মাসিক সমন্বয় সভার কার্যবিবরণী",
                            formatting = BlockFormatting(bold = true, alignment = TextAlignOption.CENTER, fontSizeSp = 20f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "তারিখ: ২৪ অক্টোবর ২০২৬ | সময়: সকাল ১০:৩০ | স্থান: কনফারেন্স রুম ১"
                        ),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "উপস্থিত সদস্যবৃন্দ:"
                        ),
                        DocumentBlock(type = BlockType.BULLET_LIST, text = "সভাপতি: মো: ফারুক হোসেন"),
                        DocumentBlock(type = BlockType.BULLET_LIST, text = "সদস্য সচিব: পলক নন্দী"),
                        DocumentBlock(type = BlockType.BULLET_LIST, text = "কোষাধ্যক্ষ: সায়মা জাহান"),
                        DocumentBlock(
                            type = BlockType.HEADING_2,
                            text = "গৃহীত সিদ্ধান্তসমূহ:"
                        ),
                        DocumentBlock(type = BlockType.NUMBERED_LIST, text = "আগামী মাসের মধ্যে লেখনী ওপেন সোর্স ভার্সন ১.০ প্রকাশ করা হবে।"),
                        DocumentBlock(type = BlockType.NUMBERED_LIST, text = "সকল বিভাগীয় নথিপত্রে বাংলা ইউনিকোড ফন্ট ব্যবহার বাধ্যতামূলক করা হবে।")
                    )
                )
            }
        ),
        TemplateItem(
            id = "invoice",
            nameEn = "Invoice",
            nameBn = "চালানপত্র (Invoice)",
            descriptionEn = "Billed items, quantity, price, and totals",
            descriptionBn = "পণ্যের বিবরণ, পরিমাণ, একক দর ও মোট বিল",
            iconName = "receipt_long",
            documentFactory = {
                LekhoniDocument(
                    title = "বাণিজ্যিক চালানপত্র নং-১০৮৪",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "চালানপত্র / বিল (INVOICE)",
                            formatting = BlockFormatting(bold = true, alignment = TextAlignOption.CENTER, fontSizeSp = 22f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "চালান নং: INV-2026-084\nতারিখ: ২৪ অক্টোবর ২০২৬\nগ্রাহক: বাংলাদেশ শিক্ষা গবেষণা ব্যুরো",
                            formatting = BlockFormatting(lineSpacingMultiplier = 1.3f)
                        ),
                        DocumentBlock(
                            type = BlockType.TABLE,
                            tableData = TableData(
                                rows = listOf(
                                    listOf("ক্রম", "বিবরণ", "পরিমাণ", "একক দর (৳)", "মোট (৳)"),
                                    listOf("০১", "লেখনী অফিস প্যাকেজ লাইসেন্স", "১টি", "০.০০", "বিনামূল্যে (মুক্ত)"),
                                    listOf("০২", "বাংলা ফন্ট ইনস্টলেশন ও কাস্টমাইজেশন", "২টি", "৫,০০০", "১০,০০০"),
                                    listOf("০৩", "কর্মী প্রশিক্ষণ কর্মশালা", "১টি", "১৫,০০০", "১৫,০০০"),
                                    listOf("", "সর্বমোট পরিশোধযোগ্য", "", "", "২৫,০০০ ৳")
                                ),
                                colCount = 5
                            )
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "কথায়: পঁচিশ হাজার টাকা মাত্র।\nধন্যবাদান্তে,\nহিসাব বিভাগ, লেখনী প্রকল্প",
                            formatting = BlockFormatting(italic = true)
                        )
                    )
                )
            }
        ),
        TemplateItem(
            id = "table_document",
            nameEn = "Table-based Document",
            nameBn = "ছকবদ্ধ নথি",
            descriptionEn = "Structured tabular data, records, and summaries",
            descriptionBn = "কাঠামোবদ্ধ তথ্য সারণী, রেকর্ড ও হিসাব",
            iconName = "table_chart",
            documentFactory = {
                LekhoniDocument(
                    title = "মাসিক প্রকল্প পর্যালোচনা সারণী",
                    blocks = listOf(
                        DocumentBlock(
                            type = BlockType.HEADING_1,
                            text = "উন্নয়ন প্রকল্পের অগ্রগতি সারণী",
                            formatting = BlockFormatting(bold = true, fontSizeSp = 20f)
                        ),
                        DocumentBlock(
                            type = BlockType.PARAGRAPH,
                            text = "প্রকল্পের নাম: জাতীয় উন্মুক্ত বাংলা নথিকরণ কর্মসূচি"
                        ),
                        DocumentBlock(
                            type = BlockType.TABLE,
                            tableData = TableData(
                                rows = listOf(
                                    listOf("মডিউল", "দায়িত্বপ্রাপ্ত", "বর্তমান অবস্থা", "লক্ষ্যমাত্রা"),
                                    listOf("বাংলা টেক্সট ইঞ্জিন", "পলক নন্দী", "সম্পন্ন (১০০%)", "অক্টোবর"),
                                    listOf("পিডিএফ রেন্ডারিং", "কোর টিম", "চলমান (৮৫%)", "নভেম্বর"),
                                    listOf("ক্লাউড ব্যাকআপ", "ইনফ্রা টিম", "পরিকল্পনাধীন", "ডিসেম্বর")
                                ),
                                colCount = 4
                            )
                        )
                    )
                )
            }
        )
    )
}
