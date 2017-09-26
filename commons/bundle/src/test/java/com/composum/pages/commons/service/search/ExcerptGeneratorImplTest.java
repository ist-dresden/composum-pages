package com.composum.pages.commons.service.search;

import com.composum.pages.commons.service.search.ExcerptGenerator;
import com.composum.pages.commons.service.search.ExcerptGeneratorImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
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
                {"ignored", "description", "... a <b>description</b> ..."},
                {"ignored", "some title", "... <b>some</b> <b>title</b> with <b>some</b>thing ..."},
                {"ignored", "another title", "... <b>another</b> <b>title</b> ..."},
                {"ignored", "another title another title AnOtHeR tItLe", "... <b>another</b> <b>title</b> ..."},
                {"ignored", "AnOtHeR tItLe", "... <b>another</b> <b>title</b> ..."},
                {"ignored", "*not*r *ItL*", "... <b>another</b> <b>title</b> ..."},
                {LONGTEXT, "example Umlauts", "... Thüs is a long <b>example</b> text which we can use to check " +
                        "excerpt ... several excerpts. We include also some <b>Umlauts</b> to check we can" +
                        " search for them: für äußerst ..."},
                {LONGTEXT, "example AND check OR query", "... Thüs is a long <b>example</b> text which we can use " +
                        "to <b>check</b> excerpt generation when the <b>query</b> words do span more than one excerpt" +
                        " " +
                        "since ..."},
                {LONGTEXT, "\"long  example\"",
                        "... Thüs is a <b>long example</b> text which we can use to check excerpt ..."},
                {LONGTEXT, "Thüs AND für", "... <b>Thüs</b> is a long example text which we can use ... Umlauts " +
                        "to check we can search for them: <b>für</b> äußerst höchstens. ..."},
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
