export type Game = {
  identifier: string;
  title: string;
  url: string;
  image: string;
  platform: "STEAM" | "EPIC";
  initialPrice: number;
  discountPrice: number;
  discountPercent: number;
};
