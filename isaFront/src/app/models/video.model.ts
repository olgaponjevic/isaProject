export interface VideoResponse {
  id: number;
  title: string;
  description: string;
  thumbnailUrl: string;
  videoUrl: string;
  createdAt: string;
  latitude: number | null;
  longitude: number | null;
  authorUsername: string;
  authorId: number;
  views: number;
  tags: string[];
}