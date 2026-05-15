export interface CommentResponse {
  id: number;
  content: string;
  createdAt: string;
  authorUsername: string;
  authorId: number;
}

export interface CommentPageResponse {
  comments: CommentResponse[];
  currentPage: number;
  totalPages: number;
  totalComments: number;
}