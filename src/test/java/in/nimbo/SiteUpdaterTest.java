package in.nimbo;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class SiteUpdaterTest {
    public static SiteUpdater siteUpdater;

    @BeforeClass
    public static void init() throws MalformedURLException {
        siteUpdater  = new SiteUpdater(new URL("http://www.tabnak.ir/fa/rss/allnews"));
    }

    @Test
    public void extractTextByPattern() throws IOException {
        String text  = siteUpdater.extractTextByPattern(new URL("http://www.tabnak.ir/fa/news/814748/%D8%A7%D9%86%D8%AA%D9%82%D8%A7%D9%84-%D9%86%D8%AF%D8%A7%D9%86%D9%85-%DA%A9%D8%A7%D8%B1%DB%8C%E2%80%8C%D9%87%D8%A7-%D8%A7%D8%B2-%DA%AF%D8%B0%D8%B4%D8%AA%D9%87-%D8%A8%D9%87-%D8%A2%DB%8C%D9%86%D8%AF%D9%87-%D8%A8%D9%87-%D9%85%D8%AF%D8%AF-%D9%85%D8%B3%D8%A6%D9%88%D9%84%D8%A7%D9%86"),
                 ".gutter_news > div.body",
                new String[0]
        );
        String expected = "اگر بحران آب مشکلی است که در سایه برخی ندانم کاریها در گذشته بروز کرده، اکنون با شرایطی مواجهیم که برای مقابله با شدت گرفتن بحران، اقدامات اساسی و کلیدی نیاز است؛ اقداماتی که ظاهرا قرار نیست رخ دهد. به گزارش «تابناک»، در حالی که بحران آب روز به روز وسیعتر میشود و آثار و نشانه هایش بیش از پیش آشکار میشود، نیم نگاهی به عملکرد مسئولان کافی است تا بر نگرانی هایمان افزوده شود؛ مسئولانی که ظاهرا هنوز درکی از مشکل بروز کرده و ابعاد رو به گسترش ندارند که اگر داشتند، موضع دیگری غیر از انفعال اتخاذ میکردند! ایران مدتهاست که به جمع کشورهای خشک دنیا پیوسته، اما مصرف سرانه آب در ایران ۴ برابر متوسط جهانی است. میزان هدررفت آب در بخش کشاورزی کشورمان بیش از ۷۰ درصد است. هنوز هم در ساختارهای آبرسانی شهری و روستایی جای تصفیه آب و تلاش برای استفاده از آب بازیافتی خالی است و از آن مهم تر، هدررفت در نظام توزیع به حدی است که باور ناپذیر به نظر میرسد. همه اینها در حالی است که طرحهای انتقال و ذخیرهسازی آب در کشور بویژه طرحهای بین اقلیمی هنوز بدون پیوست زیست محیطی و ارزیابیهای بلند مدت و کوتاه مدت تأثیرات اقلیمی انجام میشود تا در یک کلام بتوان گفت بیتدبیری در مصرف آب و مواجهه با چالش آب فریاد میزند. بی تدبیری ای که در سایه آن مشکلات و معضلات فراوانی بروز و نمود مییابند که تخلیه روستاها و افزایش شهرنشینی و کوچ از برخی شهرها به بعضی شهرهای برخوردار تنها نمونه های کوچکی از آنهاست. اوضاعی نامطلوب که اگر به اختلاف افکنی های طرح های انتقال آب و دودستگی های ناشی از آنها دقت کنیم، ترسناک جلوه میکند. آنقدر ترسناک که بسیاری از مردم فکر میکنند در توزیع عادلانه آب در حقشان اجحاف شده و اگر به تنگنا و دشواریای دچارند، نتیجه تصمیمات غلط مسئولان است. اما کدام تصمیمات و بر مبنای کدام قانون؟ آیا اتفاقی غیرقانونی موجب شده که اوضاع آب در کشورمان بحرانی شود یا وضعیت فعلی ناشی از اعمال قوانین مشکل ساز یا عمل به سلیقه در سکوت قانون است؟ آن گونه که مسئولان و نمایندگان مجلس میگویند، در خصوص مسائل آب در کشور با کمبود و خلا قانونی مواجه نیستیم و ریشه مشکلات در اجرای قوانین است. ادعایی که یحیی کمالیپور، نایب رئیس دوم کمیسیون حقوقی و قضایی مجلس شورای اسلامی در تشریح آن به ایران میگوید: «قوانین موجود در حوزه آب به میزان کافی وجود دارد به گونهای که در کنار قانون توزیع عادلانه آب، قوانین متفرقه دیگری نیز همچون استفاده و حفر چاههای غیرمجاز و ... را هم در کشور داریم. واقعیت این است که یکی از چالشهای اصلی فراروی کشورمان موضوع آب است و با وجود داشتن قوانین اصولی و محکم در این زمینه، متأسفانه اجرای آنها در حد تصویب باقی میمانند یا اجرا نمیشوند یا ناقص و با انحراف مسیر اجرایی به سرانجام میرسند.» اما این مدعا تا چه اندازه با واقعیت همخوانی دارد؟ سخنان این نماینده که از قضا حوزه انتخابیه اش، یعنی جیرفت، در مجاورت یکی از کانون های بحرانی آب در کشورمان است، در این خصوص بسیار جالب توجه است: «هماکنون در مناطق مختلف کشور شاهد این چالشها در مورد بحرانهای آبی هستیم. بهعنوان مثال روزگاری کشاورزان و باغداران جنوب استان کرمان حقابه رودخانه جاری را داشتند که با آن امرار معاش میکردند، اما این رودخانه را به روی مردم بستنند و ایشان هم که تمام زندگی آنان یک باغ یا مزرعه بوده، بواسطه بسته شدن آب زمین هایشان، برای متلاشی نشدن خانوادهایشان و برای اینکه حاشیه نشین نشوند، اقدام به حفر چاههای غیرمجاز کردهاند به گونهای که هماکنون در جنوب استان کرمان ۱۲ هزار حلقه چاه غیرمجاز داریم.» چاه های غیرمجازی که برداشت بی رویه آب از آنها طی سالهای متمادی موجب شده تا سطح آب های زیرزمینی به شدت پایین برود و منطقه در آستانه بحرانی بی مثال قرار گیرد. بحران فقدان آب آشامیدنی که از هم اکنون نیز در برخی مناطق بروز کرده و به شدت نگران کننده است. کابوسی که برای جلوگیری از بروز آن میبایست دست به اقدامات متعددی زد که از جمله آنها، تعیین تکلیف چاه های مجاز و غیرمجاز است. بله؛ حتی تعیین تکلیف چاه های مجاز! کافی است از این منظر به ماجرا نگاه کنیم که منابع آبی در برخی مناطق به قدر شرب محدود شده و توزیع عادلانه این مایع حیاتی -که از قضا از زمره انفال به شمار میآید،- ایجاب میکند تا برداشت آب محدود شود که در این صورت، چاه های مجاز نیز مشمول خواهند بود و میبایست بسته شوند. اتفاقی که لازمه رقم زدن آن تدارک ساز و کارهای مورد نیاز قانونی و علاوه بر آن، تدبیر کردن فرجام بهره مندان از این چاه ها خواهد بود. نقشی که مجلس میبایست ایفا کند اما تا این لحظه در دستور کار این قوه قرار نگرفته است. ماجرا زمانی عجیب تر میشود که بدانیم انبوه چاه های حفر شده در گذشته با اتخاذ یک تصمیم غلط در مجلس و دولت به صدور مجوز برای این چاه ها گره خورده و تا کنون هیچ تصمیمی برای اصلاح این اشتباه اتخاذ نشده است؛ اشتباهی که در رقم زدن آن دولت و مجلس وقت مقصر بوده اند اما اسلاف ایشان هم برای تصحیحش اقدامی صورت نمیدهند. انگار نه انگار که به وضوح مشخص شده امثال قانون تعیین تکلیف چاههای فاقد پروانه، مصوب سال ۱۳۸۹ از جمله قوانینی هستند که به شدت به بروز بحران فعلی دامن زده اند. به این قانون، قوانین پر اشکال دیگری که نتیجه اش احداث سازه های فراوان و اجرای طرح های انتقال آب برای رساندن آب به بخش کشاورزی و هدررفت بسیار بسیار زیاد آب در این بخش را موجب شده میتوان افزود که بسیاری از کارشناسان و منتقدان از آنها به عنوان ندانم کاری های مسئولان در گذشته یاد میکنند؛ ندانم کاری هایی که ظاهرا قرار است به آینده منتقل شوند و این را میشود از انفعال این روزهای مسئولان در زمینه برداشت های بی رویه از منابع آب در کشور در آستانه بی سابقه ترین بحران آب کشور دریافت!";
        assertEquals(expected,text);
    }
}