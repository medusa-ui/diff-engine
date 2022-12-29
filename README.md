# Custom diff engine for Medusa UI (experimental)
To answer the specific needs for a simple diff engine, and after much experimentation with XMLUnit, Java diff utils, etc - we built a custom diff engine for Medusa. It is unlikely to be perfect, but intended to be simple and fast; and specifically fulfill the needs for Medusa. It is doubtful you will find this component useful outside of Medusa UI internals' context.

## Goal
- It must contain a simple model class which can represent diffs of different types; we call this 'ServerSideDiff'
- It must expose a method that can simply take two HTML strings ('old' vs 'new') and generate a set of ServerSideDiffs for all the differences

Using the old HTML, you should be able to apply each ServerSideDiff in order and end up with the new HTML. 

We want this 100% reproducable in code. No need to first render to a page and review there, it must first work within the code itself.

We do not need to be as efficient as possible a diff engine. After all, this has to generate diffs between Thymeleaf pages, which are generally *mostly* the same.

It must be able to deal with HTML structure, as well as text nodes and attributes. We can assume valid XHTML.

ServerSideDiff must represent themselves in function of being able to be re-rendered. As such, they must contain information on where to find existing tags through XPath or relatively, where to add new nodes (before a certain xpath, after one, within a parent node).

## Implementation approach

We split the problem of reviewing the entire HTML into layers. 

For example the HTML:

    <body>
      <section>
        <div></div>
      </section>
      <p>
        <span></span>
      </p>
    </body>
    
Exists out of 3 layers:
- body
- section, p
- div, span

We apply a delta determination on each layer to fix structure first. Once structure is ok, we find deltas on text node changes and attribute changes.

Delta determination is done recursively. As such, it's goal is to not find all deltas but to find the first one it can, then loop it back find the next one. Each time, it applies its change to the original input, so that there should be less and less deltas until it is fully resolved. 

Instead of changes, we opt for removal/add. So for example:

    <p> -> <div>
    
Is traditionally in diff systems a single delta, typed 'change' which changes the p to a div. In our system, this resolves into 2 deltas: A removal of p and an addition of div. This is because we find that less types breeds less complexity.

In general, we do our delta determination in this order:
- Find all items to remove (present in old but not in new)
- Find all items to add (present in new but not in old)
- Review order and make corrections if needed (this only triggers new removals, which in turn can trigger more additions)


