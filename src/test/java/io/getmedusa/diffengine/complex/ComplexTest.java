package io.getmedusa.diffengine.complex;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ComplexTest extends DiffEngineTest {

    @Test
    void testComplex1() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>B</p>
                </section>""";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex1B() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                    <p>C</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>1</p>
                    </div>
                    <p>B</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>C</p>
                </section>""";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex2() {
        String oldHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <p>7</p>" +
                "  </section>";

        final String newHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div><p>2</p></div>" + //<- new
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <div><p>6</p></div>" + //<- new
                "   <p>7</p>" +
                "  </section>";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex3() {
        String oldHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div>" +
                "    <p>2</p>" +
                "   </div>" +
                "   <h5>5</h5>" +
                "   <div>" +
                "    <p>6</p>" +
                "   </div>" +
                "  </section>";

        final String newHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div>" +
                "    <p>2</p>" +
                "   </div>" +
                "   <p>3</p>" + //<- new
                "   <h5>5</h5>" +
                "   <div>" +
                "    <p>6</p>" +
                "   </div>" +
                "   <p>7</p>" + //<- new
                "  </section>";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex4() {
        String oldHTML = """
                <section>
                    <span>1</span>
                    <h5>1 <code>th:if</code>Above button</h5>
                    
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div><button>4 change</button>
                    </div>
                    <h5>5 <code>th:if</code>Below button</h5>
                    
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <span>2</span>
                    <h5>1 <code>th:if</code>Above button</h5>
                    <div>
                    <p>2 TOP If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                    </div>
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div><button>4 change</button>
                    </div>
                    <h5>5 <code>th:if</code>Below button</h5>
                    <div>
                    <p>6 TOP If top is <code>true</code> this should be visible on <code>top</code> of the page, but below the button</p>
                    </div>
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex7() {
        String oldHTML = """
                <section>
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div>
                        <button>4 change</button>
                    </div>
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <div>
                        <p>2 TOP If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                    </div>
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div>
                        <button>4 change</button>
                    </div>
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex8() {
        String oldHTML = """
        <body>
            <section>
            <div>
                <p>If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
            </div>
            <p>If middle is <code>true</code> this should be visible between <code>top</code> and <code>bottom</code></p>
            <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
            <div>
                <button onclick="_M.doAction(event, '__FRAGMENT__', `change()`)">change</button>
            </div>
            <div>
                <p>If top is <code>true</code> this should be visible on <code>top</code>, after the button</p>
            </div>
            <p>If middle is <code>true</code> this should be visible between <code>top</code> and <code>bottom</code>, after the button</p>
            <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code>, after the button</p>
            </section>
        </body>
        """;

        final String newHTML = """
        <body>
            <section>
                <div>
                    <p>If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                </div>
                <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                <div>
                    <button onclick="_M.doAction(event, '__FRAGMENT__', `change()`)">change</button>
                </div>
                <div>
                    <p>If top is <code>true</code> this should be visible on <code>top</code>, after the button</p>
                </div>
                <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code>, after the button</p>
            </section>
        </body>
        """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs, true);
    }

    @Test
    void testComplex9() {
        String oldHTML = """
                        <!DOCTYPE html>
                        <html lang="en">
                         <head>
                          <meta charset="UTF-8" />
                          <title>Th If Problem?</title>
                          <style>
                                code {
                                    color: crimson;
                                    font-weight: bold;
                                    padding: .25rem;
                                }
                            </style>
                         </head>
                         <body>
                          <section>
                           <div>
                            <p>If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                           </div>\s
                           <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                           <div><button onclick="_M.doAction(event, '__FRAGMENT__', `change()`)">change</button>
                           </div>
                           <div>
                            <p>If top is <code>true</code> this should be visible on <code>top</code>, after the button</p>
                           </div>\s
                           <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code>, after the button</p>
                          </section>
                         <div id="m-top-load-bar" class="progress-line" style="display:none;"></div>
                        <style>
                        div#m-top-load-bar {
                            position: fixed;
                            top: 0;
                            left: 0;
                            right: 0;
                            width: 100%;
                        }
                        .progress-line, .progress-line:before {
                            height: 3px;
                            width: 100%;
                            margin: 0;
                        }
                        .progress-line {
                            background-color: #7a00ff;
                            display: -webkit-flex;
                            display: flex;
                        }
                        .progress-line:before {
                            background-color: #f4abba;
                            content: '';
                            -webkit-animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                            animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                        }
                        @-webkit-keyframes running-progress {
                            0% { margin-left: 0px; margin-right: 100%; }
                            50% { margin-left: 25%; margin-right: 0%; }
                            100% { margin-left: 100%; margin-right: 0; }
                        }
                        @keyframes running-progress {
                            0% { margin-left: 0px; margin-right: 100%; }
                            50% { margin-left: 25%; margin-right: 0%; }
                            100% { margin-left: 100%; margin-right: 0; }
                        }
                        </style>
                        <div id="m-full-loader" style="display:none;">Loading ...</div>
                        <style>
                        div#m-full-loader {
                            background: #0000006e;
                            position: fixed;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 100%;
                            text-align: center;
                            padding-top: 15%;
                        }
                        </style>
                        <template id="m-template-button-load">
                            <span class="m-button-loader"><span></span><span></span><span></span><span></span></span>
                        </template>
                        <style>
                                .m-button-loader {
                                    display: inline-block;
                                    position: relative;
                                    width: 1em;
                                    height: 1em;
                                    -webkit-font-smoothing: antialiased;
                                    -moz-osx-font-smoothing: grayscale;
                                    opacity: 0.6;
                                    margin-right: 0.25em;
                                }
                                .m-button-loader span {
                                    box-sizing: border-box;
                                    display: block;
                                    position: absolute;
                                    width: 1em;
                                    height: 1em;
                                    border: 0.15em solid;
                                    border-radius: 50%;
                                    animation: loading-spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
                                    border-color: gray transparent transparent transparent;
                                }
                                .m-button-loader span:nth-child(1) {
                                    animation-delay: -0.45s;
                                }
                                .m-button-loader span:nth-child(2) {
                                    animation-delay: -0.3s;
                                }
                                .m-button-loader span:nth-child(3) {
                                    animation-delay: -0.15s;
                                }
                                @keyframes loading-spin {
                                    0% {
                                        transform: rotate(0deg);
                                    }
                                    100% {
                                        transform: rotate(360deg);
                                    }
                                }
                            </style>
                        <script src="/websocket.js"></script><script>_M.controller = 'th-if'; _M.sessionId = '9218172095260071d8265e9ae0483a863ba333e09958c971643'; _M.wsURL = '/socket';_M.wsP = '828b9ea215b08f543460d99e39dc8183f9890a141b8005008ed741e6b336906';</script>
                        </body>
                        </html>
                """;

        final String newHTML = """
                        <!DOCTYPE html>
                        <html lang="en">
                         <head>
                          <meta charset="UTF-8" />
                          <title>Th If Problem?</title>
                          <style>
                                code {
                                    color: crimson;
                                    font-weight: bold;
                                    padding: .25rem;
                                }
                            </style>
                         </head>
                         <body>
                          <section>
                           <div>
                            <p>If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                           </div>\s
                            <p>If middle is <code>true</code> this should be visible between <code>top</code> and <code>bottom</code></p>\s
                          \s
                           <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                           <div><button onclick="_M.doAction(event, '__FRAGMENT__', `change()`)">change</button>
                           </div>
                           <div>
                            <p>If top is <code>true</code> this should be visible on <code>top</code>, after the button</p>
                           </div>\s
                            <p>If middle is <code>true</code> this should be visible between <code>top</code> and <code>bottom</code>, after the button</p>\s
                          \s
                           <p>If bottom is <code>true</code> this should be visible at the <code>bottom</code>, after the button</p>
                          </section>
                         <div id="m-top-load-bar" class="progress-line" style="display:none;"></div>
                        <style>
                        div#m-top-load-bar {
                            position: fixed;
                            top: 0;
                            left: 0;
                            right: 0;
                            width: 100%;
                        }
                        .progress-line, .progress-line:before {
                            height: 3px;
                            width: 100%;
                            margin: 0;
                        }
                        .progress-line {
                            background-color: #7a00ff;
                            display: -webkit-flex;
                            display: flex;
                        }
                        .progress-line:before {
                            background-color: #f4abba;
                            content: '';
                            -webkit-animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                            animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                        }
                        @-webkit-keyframes running-progress {
                            0% { margin-left: 0px; margin-right: 100%; }
                            50% { margin-left: 25%; margin-right: 0%; }
                            100% { margin-left: 100%; margin-right: 0; }
                        }
                        @keyframes running-progress {
                            0% { margin-left: 0px; margin-right: 100%; }
                            50% { margin-left: 25%; margin-right: 0%; }
                            100% { margin-left: 100%; margin-right: 0; }
                        }
                        </style>
                        <div id="m-full-loader" style="display:none;">Loading ...</div>
                        <style>
                        div#m-full-loader {
                            background: #0000006e;
                            position: fixed;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 100%;
                            text-align: center;
                            padding-top: 15%;
                        }
                        </style>
                        <template id="m-template-button-load">
                            <span class="m-button-loader"><span></span><span></span><span></span><span></span></span>
                        </template>
                        <style>
                                .m-button-loader {
                                    display: inline-block;
                                    position: relative;
                                    width: 1em;
                                    height: 1em;
                                    -webkit-font-smoothing: antialiased;
                                    -moz-osx-font-smoothing: grayscale;
                                    opacity: 0.6;
                                    margin-right: 0.25em;
                                }
                                .m-button-loader span {
                                    box-sizing: border-box;
                                    display: block;
                                    position: absolute;
                                    width: 1em;
                                    height: 1em;
                                    border: 0.15em solid;
                                    border-radius: 50%;
                                    animation: loading-spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
                                    border-color: gray transparent transparent transparent;
                                }
                                .m-button-loader span:nth-child(1) {
                                    animation-delay: -0.45s;
                                }
                                .m-button-loader span:nth-child(2) {
                                    animation-delay: -0.3s;
                                }
                                .m-button-loader span:nth-child(3) {
                                    animation-delay: -0.15s;
                                }
                                @keyframes loading-spin {
                                    0% {
                                        transform: rotate(0deg);
                                    }
                                    100% {
                                        transform: rotate(360deg);
                                    }
                                }
                            </style>
                        <script src="/websocket.js"></script><script>_M.controller = 'th-if'; _M.sessionId = '9218172095260071d8265e9ae0483a863ba333e09958c971643'; _M.wsURL = '/socket';_M.wsP = '828b9ea215b08f543460d99e39dc8183f9890a141b8005008ed741e6b336906';</script>
                        </body>
                        </html>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs, true);
    }

    @Test
    void testComplex5() {
        String oldHTML = """
                <section>
                    <div>X <code>Y</code> Z</div>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>1 <code>2</code> 3</p>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex6() {
        String oldHTML = """
                <section>
                    <div>X Z</div>
                </section>
                """;

        final String newHTML = """
                <section>
                    <div>X <code>Y</code> Z</div>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
