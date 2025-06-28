export default function formatGamePrice(price: number): string {
  const priceParts = price.toString().split(".");

  if (priceParts.length === 1) {
    return price + ",00";
  }

  if (priceParts[1].length === 1) priceParts[1] += "0";
  return priceParts.join(",");
}
