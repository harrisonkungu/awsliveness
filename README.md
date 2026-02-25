# awsliveness

Perform Liveness check and return results


# @harrison-coder/awsliveness

AWS Face Liveness Detection plugin for Capacitor Apps

## Installation

```bash
npm install @harrison-coder/awsliveness
npx cap sync



## API

<docgen-index>

* [`startLivenessCheck(...)`](#startlivenesscheck)
* [`checkAvailability()`](#checkavailability)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### startLivenessCheck(...)

```typescript
startLivenessCheck(options: { sessionId: string; region?: string; }) => Promise<{ success: boolean; status?: string; error?: string; referenceImage?: string; }>
```

| Param         | Type                                                 |
| ------------- | ---------------------------------------------------- |
| **`options`** | <code>{ sessionId: string; region?: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; status?: string; error?: string; referenceImage?: string; }&gt;</code>

--------------------


### checkAvailability()

```typescript
checkAvailability() => Promise<{ available: boolean; }>
```

**Returns:** <code>Promise&lt;{ available: boolean; }&gt;</code>

--------------------

</docgen-api>
