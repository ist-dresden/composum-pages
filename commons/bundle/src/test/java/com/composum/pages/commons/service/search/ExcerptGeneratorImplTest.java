package com.composum.pages.commons.service.search;

import com.composum.pages.commons.service.search.ExcerptGenerator;
import com.composum.pages.commons.service.search.ExcerptGeneratorImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static com.composum.sling.core.util.ResourceUtil.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Some tests for {@link ExcerptGenerator}.
 */
@RunWith(Parameterized.class)
public class ExcerptGeneratorImplTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    protected static final String LONGTEXT = "Thüs is a long example text which we can use to check excerpt " +
            "generation when the query words do span more than one excerpt since the word occurences are too wide " +
            "apart. Then we connect several excerpts. We include also some Umlauts to check we can " +
            "search for them: für äußerst höchstens.";

    @Parameterized.Parameters(name = "{1}")
    public static List<Object[]> data() throws URISyntaxException {
        return asList(new Object[][]{
                {"ignored", "description", "... a <strong class=\"search-term\">description</strong> ..."},
                {"ignored", "some title", "... <strong class=\"search-term\">some</strong> <strong class=\"search-term\">title</strong> with <strong class=\"search-term\">some</strong>thing ..."},
                {"ignored", "another title", "... <strong class=\"search-term\">another</strong> <strong class=\"search-term\">title</strong> ..."},
                {"ignored", "another title another title AnOtHeR tItLe", "... <strong class=\"search-term\">another</strong> <strong class=\"search-term\">title</strong> ..."},
                {"ignored", "AnOtHeR tItLe", "... <strong class=\"search-term\">another</strong> <strong class=\"search-term\">title</strong> ..."},
                {"ignored", "*not*r *ItL*", "... <strong class=\"search-term\">another</strong> <strong class=\"search-term\">title</strong> ..."},
                {LONGTEXT, "example Umlauts", "... Thüs is a long <strong class=\"search-term\">example</strong> text which we can use to check " +
                        "excerpt ... several excerpts. We include also some <strong class=\"search-term\">Umlauts</strong> to check we can" +
                        " search for them: für äußerst ..."},
                {LONGTEXT, "example AND check OR query", "... Thüs is a long <strong class=\"search-term\">example</strong> text which we can use " +
                        "to <strong class=\"search-term\">check</strong> excerpt generation when the <strong class=\"search-term\">query</strong> words do span more than one excerpt" +
                        " " +
                        "since ..."},
                {LONGTEXT, "\"long  example\"",
                        "... Thüs is a <strong class=\"search-term\">long example</strong> text which we can use to check excerpt ..."},
                {LONGTEXT, "Thüs AND für", "... <strong class=\"search-term\">Thüs</strong> is a long example text which we can use ... Umlauts " +
                        "to check we can search for them: <strong class=\"search-term\">für</strong> äußerst höchstens. ..."},
        });
    }

    @Parameterized.Parameter(0)
    public String testtext;

    @Parameterized.Parameter(1)
    public String searchtext;

    @Parameterized.Parameter(2)
    public String expectedExcerpt;

    @Test
    public void excerpt() throws Exception {
        Resource resource = context.build().resource(context.uniqueRoot().content()).resource("test",
                PROP_PRIMARY_TYPE, TYPE_UNSTRUCTURED,
                PROP_MIXINTYPES, new Object[]{TYPE_CREATED, TYPE_LAST_MODIFIED, TYPE_TITLE, TYPE_VERSIONABLE},
                PROP_RESOURCE_TYPE, "cpp:Page", "title", "some title with something",
                PROP_TITLE, "another title", PROP_DESCRIPTION, "a description",
                PROP_DATA, testtext
        ).commit().getCurrentParent();
        ExcerptGeneratorImpl excerptGenerator = new ExcerptGeneratorImpl();
        excerptGenerator.contextLength = 40;
        excerptGenerator.minContextLength = 1;
        String excerpt = excerptGenerator.excerpt(resource, searchtext);
        assertEquals(expectedExcerpt, excerpt);
        excerpt = excerptGenerator.excerpt(asList(resource, resource), searchtext);
        assertEquals(expectedExcerpt, excerpt);
    }

}
