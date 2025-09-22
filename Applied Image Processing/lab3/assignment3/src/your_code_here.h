#pragma once
#include <algorithm>
#include <array>
#include <cmath>
#include <numeric>
#include <tuple>
#include <vector>

#include "helpers.h"


/*
 * Utility functions.
 */

/// <summary>
/// Bilinearly sample ImageFloat or ImageRGB using image coordinates [x,y].
/// Note that the coordinate start and end points align with the entire image and not just
/// the pixel centers (a half-pixel difference).
/// </summary>
/// <typeparam name="T">template type, can be ImageFloat or ImageRGB</typeparam>
/// <param name="image">input image</param>
/// <param name="pos">x,y position in px units</param>
/// <returns>interpolated pixel value (float or glm::vec3)</returns>
template <typename T>
inline T sampleBilinear(const Image<T>& image, const glm::vec2& pos_px)
{
    // Write a code that bilinearly interpolates values from a generic image (can contain either float or glm::vec3).
    // The pos_px input represents the (x,y) pixel coordinates of the sampled point where:
    //   [0, 0] = The left top corner of the left top (=first) pixel.
    //   [width, height] = The right bottom corner of the right bottom (=last) pixel.
    //   [0, height] = The left bottom corner of the left bottom pixel.
    // 
    // Take into account the size of individual pixel and the fact, that the "value" of pixel is conceptually stored in its center.
    //      => Example 1: For pos_px between centers of pixels, the method bilinearly interpolates between 4 nearest pixel.
    //      => Example 2: For pos_px corresponding to a center of a pixel, the method needs to return an exact value of that pixel. 
    //                    This is a natural property of any interpolation.
    // 
    // Therefore, steps are as follows:
    //     1. Determine the 4 nearest pixels.
    //     2. Bilinearly interpolate their values based on the position of the sampling point between them.
    // 
    // Note: The method is templated by parameter "T". This will be either float or glm::vec3 depending on whether the method
    // is called with ImageFloat or ImageRGB. Use either "T" or "auto" to define your variables and use glm::functions to handle both types.
    // Example:
    //    auto value = image.data[0] * 3; // both float and glm:vec3 support baisc operators
    //    T rounded_value = glm::round(image.data[0]); // glm::round will handle both glm::vec3 and float. 
    // Use glm API for further reference: https://glm.g-truc.net/0.9.9/api/a00241.html
    // 
    
    //
    //    YOUR CODE GOES HERE
    //

    //if (glm::round(pos_px.x) == pos_px.x && glm::round(pos_px.y) == pos_px.y) {
        //printf("%f %f\n", pos_px.x, pos_px.y);

        //return image.data[pos_px.y * (image.width) + pos_px.x];
    //} else {
        auto x = pos_px.x;
        auto y = pos_px.y;

        auto x1 = glm::floor(pos_px.x - 0.5f);
        auto x2 = x1 + 1;
        if (x1 < 0) {
            x1 = 0;
        }
        if (x2 < 0) {
            x2 = 0;
        }
        if (x2 >= image.width) {
            x2 = image.width - 1;
        }if (x1 >= image.width) {
            x1 = image.width - 1;
        }
        auto y1 = glm::floor(pos_px.y - 0.5f);
        auto y2 = y1 + 1;
        if (y1 < 0)
            y1 = 0;
        if (y1 >= image.height) {
            y1 = image.height - 1;
        }
        if (y2 >= image.height) {
            y2 = image.height - 1;
        }
        if (y2 < 0) {
            y2 = 0;
        }


        //printf("x:%f y:%f\n", x, y);

        //printf("x1:%f y1:%f x2:%f y2:%f\n", x1, y1, x2, y2);

        //auto fX = image.data[pos_px.y * (image.width) + pos_px.x];

        auto Alpha = glm::abs(x - (x1+0.5f));
        auto Beta = glm::abs((y2+0.5f) - y);

        auto fA = image.data[y2 * image.width + x1];
        auto fB = image.data[y2 * image.width + x2];
        auto fC = image.data[y1 * image.width + x2];
        auto fD = image.data[y1 * image.width + x1];

        auto fX0 = fA * (1 - Alpha) + fB * Alpha;
        auto fX1 = fD * (1 - Alpha) + fC * Alpha;

        auto fX = fX0 * (1 - Beta) + fX1 * Beta; 

        return fX;
    //}

     // <-- Change this.
}



/*
  Core functions.
*/

/// <summary>
/// Applies the bilateral filter on the given disparity image.
/// Ignored pixels that are marked as invalid.
/// </summary>
/// <param name="disparity">The image to be filtered.</param>
/// <param name="guide">The image guide used for calculating the tonal distances between pixel values.</param>
/// <param name="size">The kernel size, which is always odd.</param>
/// <param name="guide_sigma">Sigma value of the gaussian guide kernel.</param>
/// <returns>ImageFloat, the filtered disparity.</returns>
ImageFloat jointBilateralFilter(const ImageFloat& disparity, const ImageRGB& guide, const int size, const float guide_sigma)
{
    // The filter size is always odd.
    assert(size % 2 == 1);

    // We assume both images have matching dimensions.
    assert(disparity.width == guide.width && disparity.height == guide.height);

    // Rule of thumb for gaussian's std dev. 
    const float sigma = (size - 1) / 2 / 3.2f;

    // Empty output image.
    auto result = ImageFloat(disparity.width, disparity.height);
    #pragma omp parallel for
    for (int x = 0; x < result.width ; x++) {
        for (int y = 0; y < result.height; y++) {
            int n_valid = 0;
            float weights = 0;
            float weights_pixels = 0;
            
            for (int j = x - ((size - 1) / 2); j <= x + ((size - 1) / 2); j++) {
                for (int k = y - ((size - 1) / 2); k <= y + ((size - 1) / 2); k++) {
                    if (j >= 0 && k >= 0 && j < result.width && k < result.height) {
                        
                        if (disparity.data[(k * disparity.width + j)] != INVALID_VALUE) {
                            float dist = std::sqrt(std::pow(x - j, 2) + std::pow(y - k, 2));

                            float diff_value = std::sqrt(std::pow(guide.data[y * guide.width + x].r - guide.data[(k * guide.width + j)].r, 2) + 
                                std::pow(guide.data[y * guide.width + x].g - guide.data[(k * guide.width + j)].g, 2) + std::pow(guide.data[y * guide.width + x].b - guide.data[(k * guide.width + j)].b, 2));

                            float w_i = gauss(dist, sigma) * gauss(diff_value, guide_sigma);

                            weights_pixels += w_i * disparity.data[(k * disparity.width + j)];
                            weights += w_i;
                            n_valid++;
                        } 
                    }
                }
            }

            if (n_valid == 0) {
                result.data[y * guide.width + x] = INVALID_VALUE;
            } else {
                result.data[y * guide.width + x] = weights_pixels / weights;
            }


        
        }
        
    }


    //
    // Implement a bilateral filter of the disparity image guided by the guide.
    // Ignore all contributing pixels where disparity == INVALID_VALUE.
    //
    // 1. Iterate over all output pixels.
    // 2. For each output pixel, visit all neighboring pixels (including itself) in a size x size symmetric neighborhood. 
    //    That is the same as during convolution with a size x size symmetric filter centered around the pixel.
    // 3. If a neighbor is outside of the image or its disparity == INVALID_VALUE, skip the pixel (move to the next one).
    // 4. For each neighbor compute its weight as w_i = gauss(dist, sigma) * gauss(diff_value, guide_sigma)
    //      where
    //          * dist is the Eucledian distance between the center and current pixel in pixels (L2 norm).
    //          * diff_value is the L2 (euclidean) distance of the guide image pixel values for the center and current pixel.
    //          * gauss(x, sigma) is a Normal distribution pdf function for x=x and std.dev.= sigma which is available for use.
    // 5. Compute weighted mean of all (unskipped) neighboring pixel disparities and assign it to the output.
    // 
    // Refer to the first Lecture or recommended study books for more info on Bilateral filtering.
    // 
    // Notes:
    //   * If a pixel has no neighbor (all were skipped), assign INVALID_VALUE to the output.  
    //   * One point awarded for a correct OpenMP parallelization.

    //
    //    YOUR CODE GOES HERE
    //

    auto example = gauss(0.5f, 1.2f); // This is just an example of computing Normal pdf for x=0.5 and std.dev=1.2.


    // Return filtered disparity.
    return result;
}

/// <summary>
/// In-place normalizes and an ImageFloat image to be between 0 and 1.
/// All values marked as invalid will stay marked as invalid.
/// </summary>
/// <param name="scalar_image"></param>
/// <returns></returns>
void normalizeValidValues(ImageFloat& scalar_image)
{
    //
    // Find minimum and maximum among the VALID image values.
    // Linearly rescale the VALID image values to the [0,1] range (in-place).
    // The INVALID values remain INVALID (they are ignored).
    // 
    // Note #1: Pixel is INVALID as long as value == INVALID_VALUE.
    // Note #2: This modified the input image in-place => no "return".
    //
    
    //
    //    YOUR CODE GOES HERE
    // 
    // 
    //

    float min_val = scalar_image.data[0];
    float max_val = scalar_image.data[0];


    //#pragma omp parallel for
    for (int x = 0; x < scalar_image.width; x++) {
        for (int y = 0; y < scalar_image.height; y++) {
            if (scalar_image.data[y * scalar_image.width + x] == INVALID_VALUE) {
                continue;
            }
            if (scalar_image.data[y * scalar_image.width + x] < min_val) {
                // #pragma omp critical
                min_val = std::min(min_val, scalar_image.data[y * scalar_image.width + x]);
            } else if (scalar_image.data[y * scalar_image.width + x] > max_val) {
                // #pragma omp critical
                max_val = std::max(max_val, scalar_image.data[y * scalar_image.width + x]);
            }
        }
    }

    for (int x = 0; x < scalar_image.width; x++) {
        for (int y = 0; y < scalar_image.height; y++) {
            if (scalar_image.data[y * scalar_image.width + x] == INVALID_VALUE)
                continue;
            scalar_image.data[y * scalar_image.width + x] = (scalar_image.data[y * scalar_image.width + x] - min_val) / (max_val - min_val);
        }
    }
       
    
    
}

/// <summary>
/// Converts a disparity image to a normalized depth image.
/// Ignores invalid disparity values.
/// </summary>
/// <param name="disparity">disparity in arbitrary units</param>
/// <returns>linear depth scaled from 0 to 1</returns>
ImageFloat disparityToNormalizedDepth(const ImageFloat& disparity)
{
    auto depth = ImageFloat(disparity.width, disparity.height);

    //
    // Convert disparity to a depth with unknown scale:
    //    depth_unscaled = 1.0 / disparity
    // If disparity of a pixel is invalid, set its depth also invalid (INVALID_VALUE).
    // We guarantee that all valid disparities > 0.
    //
        
    //
    //    YOUR CODE GOES HERE
    //

    // Rescales valid depth values to [0,1] range.

    for (int x = 0; x < disparity.width; x++) {
        for (int y = 0; y < disparity.height; y++) {
            if (disparity.data[y * disparity.width + x] == INVALID_VALUE) {
                depth.data[y * depth.width + x] = disparity.data[y * disparity.width + x];
            } else {
                depth.data[y * depth.width + x] = 1.0f / disparity.data[y * disparity.width + x];
            }
        }
           
    }

    normalizeValidValues(depth);

    return depth;
}

/// <summary>
/// Convert linear normalized depth to target pixel disparity.
/// Invalid pixels 
/// </summary>
/// <param name="depth">Normalized depth image (values in [0,1])</param>
/// <param name="iod_mm">Inter-ocular distance in mm.</param>
/// <param name="px_size_mm">Pixel size in mm.</param>
/// <param name="screen_distance_mm">Screen distance from eyes in mm.</param>
/// <param name="near_plane_mm">Near plane distance from eyes in mm.</param>
/// <param name="far_plane_mm">Far plane distance from eyes in mm.</param>
/// <returns>screen disparity in pixels</returns>
ImageFloat normalizedDepthToDisparity(
    const ImageFloat& depth, const float iod_mm,
    const float px_size_mm, const float screen_distance_mm,
    const float near_plane_mm, const float far_plane_mm)
{
    auto px_disparity = ImageFloat(depth.width, depth.height);

    //
    // Based on physical dimensions, distance, resolution (and hence pixel size) of the screen,
    // as well as physiologically determined distance between viewers pupil (IOD or IPD),
    // compute stereoscopic pixel disparities that will make the display appear at a correct depth
    // represented by linear interpolation between the near and far plane based on the depth input image.
    // 
    // Refer to Lecture 4 for formulas.
    // 
    // Example:
    //    screen distance = 600 mm, near_plane_mm = 550, far_plane == 650, depth = 0.1  
    //         => the pixel should appear 55+0.1(65-55) = 56 cm away from the user
    //         => That is 4 cm in front of the screen.
    //         => That means the pixel disparity will be a negative number ("crossed disparity").
    // 
    // Note:
    //    * All distances are measured orthogonal on the screen and are assumed constant across the screen (ignores the eccentricity variance).
    //    * Invalid pixels (depth==INVALID_VALUE) are to be marked invalid on the output as well.
    //
    
    //
    //    YOUR CODE GOES HERE
    //

    for (int x = 0; x < depth.width; x++) {
        for (int y = 0; y < depth.height; y++) {
            
            if (depth.data[y * depth.width + x] == INVALID_VALUE) {
                px_disparity.data[y * px_disparity.width + x] = INVALID_VALUE;
            } else {
                float relative_depth = -(screen_distance_mm - (near_plane_mm + depth.data[y * depth.width + x] * (far_plane_mm - near_plane_mm)));
                
                float pixel_disparity = (iod_mm / px_size_mm) * (relative_depth / (relative_depth + screen_distance_mm));

                px_disparity.data[y * px_disparity.width + x] = pixel_disparity;
                

            }
        }
    }

    return px_disparity; // returns disparity measured in pixels
}

/// <summary>
/// Creates a warping grid for an image of specified height and weight.
/// It produces vertex buffer which stores 2D positions of pixel corners,
/// and index buffer which defines triangles by triplets of indices into
/// the vertex buffer (the three vertices form a triangle).
/// 
/// </summary>
/// <param name="width">Image width.</param>
/// <param name="height">Image height.</param>
/// <returns>Mesh, containing a vertex buffer and triangle index buffer.</returns>
Mesh createWarpingGrid(const int width, const int height)
{

    // Build vertex buffer.
    auto num_vertices = (width + 1) * (height + 1);
    auto vertices = std::vector<glm::vec2>(num_vertices);

    //
    // Fill the vertex buffer (vertices) with 2D coordinate of the pixel corners.
    // Expected output coordinates are:
    //   [0,0] for the left top corner of the left top (=first) pixel.
    //   [width,height] for the right bottom corner of the right bottom (=last) pixel.
    //   [0,height] for the left bottom corner of the left bottom pixel.
    // 
    // The order in memory is to be the same as for images (row after row).
    // 
    
    //
    //    YOUR CODE GOES HERE
    //

    for (int x = 0; x < width +1; x++) {
        for (int y = 0; y < height+1 ; y++) {
            vertices[y * (width+1) + x] = glm::vec2(x,y);
        }
    }

    // Build index buffer.
    auto num_pixels = width * height;
    auto num_triangles = num_pixels * 2;
    auto triangles = std::vector<glm::ivec3>(num_triangles);

    //
    // Fill the index buffer (triangles) with indices pointing to the vertex buffer.
    // Each element of the "triangles" is an integer triplet (glm::ivec3). 
    // It represents a triangle by selecting 3 vertices from the vertex buffer defining its corners in
    // a clockwise manner.
    // We need to fill the index buffer in the same order as pixels are stored in memory (that is row by row)
    // and for each pixel we should generate two triangles that together cover the are of a pixel as follows:
    // 
    //   A ------- B
    //   |  *      |
    //   |    *    |
    //   |      *  |
    //   D ------- C
    // 
    // Where A,B,C,D are the CORNERS of the respective pixel.
    // 
    // For each such pixel, we add two triangles: 
    //     glm::ivec3(A,B,C) and glm::ivec3(A,C,D)  (in this exact order)
    // where A,B,C,D are indices into the vertex buffer.
    // 
    // The result should be a grid that fills an entire image and replaces each pixel with two small triangles.
    //
    
    //
    //    YOUR CODE GOES HERE
    //
    // Combine the vertex and index buffers into a mesh.

    int count = 0;

    for (int x = 0; x < width +1; x++) {
        for (int y = 0; y < height+1; y++) {

            if ((x + 1) < width + 1 && (y+1) < height + 1) {
                triangles[count] = glm::ivec3(y * (width+1) + x, y * (width+1) + (x + 1), (y + 1) * (width+1) + (x + 1));
                count++;
                triangles[count] = glm::ivec3(y * (width+1) + x, (y + 1) * (width+1) + (x + 1), (y + 1) * (width+1) + x);
                count++;
            } 
        }
    }


    return Mesh { std::move(vertices), std::move(triangles) };
}

/// <summary>
/// Warps a grid based on the given disparity and scaling_factor.
/// </summary>
/// <param name="grid">The original mesh.</param>
/// <param name="disparity">Disparity for each PIXEL.</param>
/// <param name="scaling_factor">Global scaling factor for the disparity.</param>
/// <returns>Mesh, the warped grid.</returns>
Mesh warpGrid(Mesh& grid, const ImageFloat& disparity, const float scaling_factor, const BilinearSamplerFloat& sampleBilinear)
{
    // Create a copy of the input mesh (all values are copied).
    auto new_grid = Mesh { grid.vertices, grid.triangles };

    const float EDGE_EPSILON = 1e-5f * disparity.width;

    //
    // The goal is to modify the x coordinate of the grid vertices based on 
    // the scaled pixel disparity corresponding to the original location of the vertex buffer:
    // 
    // new_grid.vertex.x = grid.vertex.x + scaling_factor * sampled_disparity
    // 
    // where sampled_disparity is a bilinearly interpolated value from the disparity image
    // which we can easily obtained using our other function "sampleBilinear":
    //     sampled_disparity = sampleBilinear(disparity, grid.vertex)
    // 
    // IMPORTANT - in order to keep the grid attached to the image "frame",
    // we must not move the border vertices => do not move vertices
    // that are within EDGE_EPSILON from the image boundary in either x or y
    // direction.
    //

    // Here is an example use of the bilinear interpolation (using the provided function argument).
    //auto interpolated_value = sampleBilinear(disparity, glm::vec2(1.0f, 1.0f));
    // Recommended test: For a 2x2 image it SHOULD return the mean of the 4 pixels.
    
    //
    //    YOUR CODE GOES HERE
    //    


    /* auto p1 = glm::vec2(4, 1);
    auto p2 = glm::vec2(1, 0);
    auto p3 = glm::vec2(2, 1);
    auto p4 = glm::vec2(1, 2);

    auto img = ImageFloat(2, 2);

    img.data[0] = 1;
    img.data[1] = 2;
    img.data[2] = 3;
    img.data[3] = 4;


    auto v1 = sampleBilinear(img, p1);
    auto v2 = sampleBilinear(img, p2);
    auto v3 = sampleBilinear(img, p3);
    auto v4 = sampleBilinear(img, p4);

    std::cout << v1 << std::endl;
    std::cout << v2 << std::endl;
    std::cout << v3 << std::endl;
    std::cout << v4 << std::endl;*/



    for (int i= 0; i < grid.vertices.size(); i++) {
        if (grid.vertices[i].x - 0 > EDGE_EPSILON && disparity.width - grid.vertices[i].x > EDGE_EPSILON) {
            auto interpolated_value = sampleBilinear(disparity, grid.vertices[i]);

            auto final_pos = grid.vertices[i].x + scaling_factor * interpolated_value;
                
            new_grid.vertices[i].x = final_pos;
        } 
        
    }
    return new_grid;
}



/// <summary>
/// Forward-warps an image based on the given disparity and warp_factor.
/// </summary>
/// <param name="src_image">Source image.</param>
/// <param name="src_depth">Depth image used for Z-Testing</param>
/// <param name="disparity">Disparity of the source image in pixels.</param>
/// <param name="warp_factor">Multiplier of the disparity.</param>
/// <returns>ImageWithMask, containing the forward-warped image and a mask image. Mask=1 for valid pixels, Mask=0 for holes</returns>
ImageWithMask forwardWarpImage(const ImageRGB& src_image, const ImageFloat& src_depth, const ImageFloat& disparity, const float warp_factor)
{
    // The dimensions of src image, src depth and disparity maps all match.
    assert(src_image.width == disparity.width && src_image.height == disparity.height);
    assert(src_image.width == disparity.width && src_depth.height == src_depth.height);
    
    // Create a new image and depth map for the output.
    auto dst_image = ImageRGB(src_image.width, src_image.height);
    auto dst_mask = ImageFloat(src_depth.width, src_depth.height);
    // Fill the destination depth mask map with zero.
    std::fill(dst_mask.data.begin(), dst_mask.data.end(), 0.0f);
    auto dst_depth = ImageFloat(src_depth.width, src_depth.height);
    // Fill the destination depth map with a very large number.
    std::fill(dst_depth.data.begin(), dst_depth.data.end(), std::numeric_limits<float>::max());

    
    #pragma omp parallel for
    for (int y = 0; y < dst_image.height; y++) {
        for (int x = 0; x < dst_image.width; x++) {
            int x_prime = std::round(x + disparity.data[y* dst_image.width + x]* warp_factor);
            int y_prime = y;

            if (x_prime >= 0 && x_prime < dst_image.width) {
                if (src_depth.data[y * src_depth.width + x] < dst_depth.data[y_prime * dst_depth.width + x_prime]) {
                    dst_image.data[y_prime * dst_image.width + x_prime] = src_image.data[y * src_image.width + x];
                    dst_depth.data[y_prime * dst_depth.width + x_prime] = src_depth.data[y * src_depth.width + x];
                    dst_mask.data[y_prime * dst_mask.width + x_prime] = 1;
                }
                
            }
        }
    }
   


    // Return the warped image.
    return ImageWithMask(dst_image, dst_mask);

}


/// <summary>
/// Applies the bilateral filter on the given image to fill the holes
/// indicated by a binary mask (mask==0 -> missing pixel).
/// Keeps the pixels not marked as holes unchanged.
/// </summary>
/// <param name="img_forward">The image to be filtered and its mask.</param>
/// <param name="size">The kernel size. It is always odd.</param>
/// <param name="guide_sigma">Sigma value of the gaussian guide kernel.</param>
/// <returns>ImageRGB, the filtered forward warping image.</returns>
ImageRGB inpaintHoles(const ImageWithMask& img, const int size)
{
    // The filter size is always odd.
    assert(size % 2 == 1);

    // Rule of thumb for gaussian's std dev.
    const float sigma = (size - 1) / 2 / 3.2f;

    // The output is initialized by copy of the input.
    auto result = ImageRGB(img.image);
   

    // The goal is to fill the holes in forward warping image using a bilateral filter where the mask serves as a guide.
    
    // 1. For valid pixels (mask >= 0.5) -> do nothing and keep the valid pixel.
    // 2. For invalid pixels -> replace pixel with:
    //      Filter valid neighbors in [size x size] in a symmetric neighborhood 
    //      with a Gaussian filter weight w_i = gauss(dist, sigma) where
    //          * dist is the Euclidean distance between the center and current pixel in pixels (L2 norm).
    //          * gauss(x, sigma) is a Normal distribution pdf function for x=x and std.dev.= sigma which is available for use.
    //      !!!! SKIP invalid neighbors (mask < 0.5) !!!!
    // 
    // Notes:
    //   * Keep pixels with no valid neighbors unmodified.

    //
    //    YOUR CODE GOES HERE
    //

    assert(img.image.width == img.mask.width && img.image.height == img.image.height);
    
    for (int x = 0; x < img.mask.width; x++) {
        for (int y = 0; y < img.mask.height; y++) {
            if (img.mask.data[y * img.mask.width + x] < 0.5) {

                float weights = 0;
                auto weights_pixels = glm::vec3();
                int n_valid = 0;

                for (int j = x - ((size - 1) / 2); j <= x + ((size - 1) / 2); j++) {
                    for (int k = y - ((size - 1) / 2); k <= y + ((size - 1) / 2); k++) {
                        if (j >= 0 && k >= 0 && j < img.mask.width && k < img.mask.height) {

                            if (img.mask.data[k * img.mask.width + j] >= 0.5) {
                                float dist = std::sqrt(std::pow(x - j, 2) + std::pow(y - k, 2));
                                float w_i = gauss(dist, sigma);
                                weights_pixels += w_i * img.image.data[(k * img.image.width + j)];
                                weights += w_i;
                                n_valid++;

                            }
                        }
                    }
                }

                if (n_valid > 0) {
                    auto final_pixel = weights_pixels / weights;

                    result.data[y * result.width + x] = final_pixel;
                } 
                

            } else {
                result.data[y * result.width + x] = img.image.data[y * img.image.width + x];
            }
        }
    }


    // Return inpainted image.
    return result;
}

/// <summary>
/// Backward-warps an image using a warped mesh.
/// </summary>
/// <param name="src_image">Source image.</param>
/// <param name="src_depth">Depth image used for Z-Testing</param>
/// <param name="src_grid">Source grid.</param>
/// <param name="dst_grid">The warped grid.</param>
/// <param name="sampleBilinear">a function that bilinearly samples ImageFloat</param>
/// <param name="sampleBilinearRGB">a function that bilinearly samples ImageRGB</param>
/// <returns>ImageRGB, the backward-warped image.</returns>
ImageRGB backwardWarpImage(const ImageRGB& src_image, const ImageFloat& src_depth, const Mesh& src_grid, const Mesh& dst_grid,
    const BilinearSamplerFloat& sampleBilinear, const BilinearSamplerRGB& sampleBilinearRGB)
{
    // The dimensions of src image and depth match.
    assert(src_image.width == src_depth.width && src_image.height == src_depth.height);
    // We assume that both grids have the same size and also the same order (ie., there is 1:1 triangle pairing).
    // This implies that the content of index buffers of both meshes are exactly equal (we do not test it here).
    assert(src_grid.triangles.size() == dst_grid.triangles.size());

    // Create a new image and depth map for the output.
    auto dst_image = ImageRGB(src_image.width, src_image.height);
    auto dst_depth = ImageFloat(src_depth.width, src_depth.height);
    // Fill the destination depth map with a very large number.
    std::fill(dst_depth.data.begin(), dst_depth.data.end(), 1e20f);

    for (int i = 0; i < dst_grid.triangles.size(); i++) {
        auto triangle = dst_grid.triangles[i];
        auto tp1 = triangle.x;
        auto tp2 = triangle.y;
        auto tp3 = triangle.z;
        
        auto min_x = std::min(std::min(dst_grid.vertices[tp1].x, dst_grid.vertices[tp2].x), dst_grid.vertices[tp3].x);
        if (min_x < 0)
            min_x = 0;
        auto max_x = std::max(std::max(dst_grid.vertices[tp1].x, dst_grid.vertices[tp2].x), dst_grid.vertices[tp3].x);
        if (max_x >= src_image.width) {
            max_x = src_image.width - 1;
        } 

        auto min_y = std::min(std::min(dst_grid.vertices[tp1].y, dst_grid.vertices[tp2].y), dst_grid.vertices[tp3].y);
        if (min_y < 0){
            min_y = 0;
        }

        auto max_y = std::max(std::max(dst_grid.vertices[tp1].y, dst_grid.vertices[tp2].y), dst_grid.vertices[tp3].y);
        if (max_y >= src_image.height) {
            max_y = src_image.height - 1;
        } 
            
        max_x = std::floor(max_x);
        min_x = std::floor(min_x);
        max_y = std::floor(max_y);
        min_y = std::floor(min_y);

        //printf("%f %f %f %f\n", min_x, max_x, min_y, max_y);

        for (auto x = min_x; x < max_x+1 ; x++) {
            for (auto y = min_y; y < max_y+1 ; y++) {
                if (isPointInsideTriangle(glm::vec2(x+0.5f, y+0.5f), dst_grid.vertices[tp1], dst_grid.vertices[tp2], dst_grid.vertices[tp3])) {
                    
                    glm::vec3 bc = barycentricCoordinates(glm::vec2(x+0.5f,y+0.5f), dst_grid.vertices[tp1], dst_grid.vertices[tp2], dst_grid.vertices[tp3]);

                    auto triangle_original = src_grid.triangles[i];

                    auto t1 = triangle_original.x;
                    auto t2 = triangle_original.y;
                    auto t3 = triangle_original.z;

                    auto pt_src = src_grid.vertices[t1]* bc.x +  src_grid.vertices[t2] * bc.y + src_grid.vertices[t3] * bc.z;

                    auto interpolated_image = sampleBilinearRGB(src_image,pt_src);

                    auto interpolated_depth = sampleBilinear(src_depth, pt_src);


                    auto destination_depth = dst_depth.data[y * dst_depth.width + x];

                    if (interpolated_depth < destination_depth) {
                        
                        dst_image.data[y * dst_image.width + x] = interpolated_image;
                        dst_depth.data[y * dst_depth.width + x] = interpolated_depth;
                    }
                }
            }
        }
    }

    //
    // This method implements mesh-based warping by rasterizing
    // each triangle of the destination grid and sampling
    // the source texture by looking up corresponding 
    // position in the source grid using barycentric coordinates.
    // 
    // 1. For every triangle in the warped grid (dst_grid), 
    //    determine X and Y ranges of destination pixel coordinates
    //    whose **centers** lie in the bounding box of the triangle.
    //    Note: Look back to createWarpingGrid() for definition
    //          of the grid vertex coordinates.
    // 
    // 2. Enumerate all candidate pixels within the bounding box.
    //    Skip pixels whose centers actually do not lie inside the triangle.
    //    Use the provided isPointInsideTriangle(pt_dst, vert_a, vert_b, vert_c)
    //    method to do the test.
    // 
    // 3. Compute barycentric coordinates of the pixel center in the triangle
    //    using the provided method bc = barycentricCoordinates(pt_dst, vert_a, vert_b, vert_c).
    // 
    // 4. Find the corresponding triangle in the original mesh. It has the same triangle index.
    // 
    // 5. Reproject the pixel center to the source grid by using the already computed barycentric
    //    coordinates:
    //        pt_src = (vert_a_src, vert_b_src, vert_c_src) * bc
    // 
    // 6. Bilinearly sample both the source depth map and the source image values at the pt_src
    //    position. 
    //    Hint: Use the sampleBilinear[RGB]() method implemented earlier (using the provided function arguments).
    // 
    // 7. Compare the depth in the source and destination depth values and implement the depth-test
    //    like in fowardWarpImage().
    //    Hint: The destination map can be accessed directly without interpolation because we are
    //          computing for an exact pixel coordinate.
    // 
    // 8. If the depth test passes, write out the destination image (dst_image) and depth (dst_depth) values. 
    //    Again, it is the same logic as in fowardWarpImage().
    // 
    // 9. Return the warped image (dst_image).
    //
    
    // Example of testing point [0.1, 0.2] is inside a triangle.
    bool is_point_inside = isPointInsideTriangle(glm::vec2(0.1, 0.2), glm::vec2(0, 0), glm::vec2(1, 0), glm::vec2(0, 1));

    // Example of computing barycentric coordinates of a point [0.1, 0.2] inside a triangle.
    glm::vec3 bc = barycentricCoordinates(glm::vec2(0.1, 0.2), glm::vec2(0, 0), glm::vec2(1, 0), glm::vec2(0, 1));

    //
    //    YOUR CODE GOES HERE
    //

    // Return the warped image.
    return dst_image;
}

/// <summary>
/// Returns an anaglyph image.
/// </summary>
/// <param name="image_left">left RGB image</param>
/// <param name="image_right">right RGB image</param>
/// <param name="saturation">color saturation to apply</param>
/// <returns>ImageRGB, the anaglyph image.</returns>
ImageRGB createAnaglyph(const ImageRGB& image_left, const ImageRGB& image_right, const float saturation)
{
    // An empty image for the resulting anaglyph.
    auto anaglyph = ImageRGB(image_left.width, image_left.height);

    // 
    // Convert stereoscopic pair into a single anaglyph stereoscopic image
    // for viewing in red-cyan anaglyph glasses.
    // We additionally scale saturation of the image to make the image
    // more "grayscale" since colors are problematic in analglyph image
    // and increase crosstalk (ghosting).
    // 
    // For both left and rigt image:
    // 1. Convert RGB to HSV color space using the provided rgbToHsv() function.
    // 2. Scale the saturation (stored in the second (=Y) component of the vec3) by the "saturation" param.
    // 3. Convert back to RGB using the hsvToRgb().
    // 
    // Combine the two images such that:
    //    * output.red = left.red
    //    * output.green = right.green
    //    * output.blue = right.blue.
    //

    // Example: RGB->HSV->RGB should be approx identity.
    auto rgb_orig = glm::vec3(0.2, 0.6, 0.4);
    auto rgb_should_be_same = hsvToRgb(rgbToHsv(rgb_orig)); // expect rgb == rgb_2 (up to numerical precision)

    //
    //    YOUR CODE GOES HERE
    //

    for (int x = 0; x < anaglyph.width; x++) {
        for (int y = 0; y < anaglyph.height; y++) {
            auto hsv_right = rgbToHsv(image_right.data[y * image_right.width + x]); 
            auto hsv_left = rgbToHsv(image_left.data[y * image_left.width + x]);

            hsv_left.y *= saturation;
            hsv_right.y *= saturation;

            auto rgb_right = hsvToRgb(hsv_right);
            auto rgb_left = hsvToRgb(hsv_left);

            anaglyph.data[y * anaglyph.width + x].r = rgb_left.r;
            anaglyph.data[y * anaglyph.width + x].g = rgb_right.g;
            anaglyph.data[y * anaglyph.width + x].b = rgb_right.b;
           
        }
    }

    // Returns a single analgyph image.
    return anaglyph;
}


/// <summary>
/// Rotates a grid counter-clockwise around the center by a given angle in degrees.
/// </summary>
/// <param name="grid">The original mesh.</param>
/// <param name="center">The center of the rotation (in pixel coords).</param>
/// <param name="angle">Angle in degrees.</param>
/// <returns>Mesh, the rotated grid.</returns>
Mesh rotatedWarpGrid(Mesh& grid, const glm::vec2& center, const float& angle)
{
    // Create a copy of the input mesh (all values are copied).
    auto new_grid = Mesh { grid.vertices, grid.triangles };

    const float DEGREE2RADIANS = 0.0174532925f;

    float radians = angle * DEGREE2RADIANS;

    //
    // The goal is to rotate the coordinate of the grid vertices
    // counter-clockwise around the 'center' by a given angle in degrees:
    //
    // 1. Create a 3*3 matrix T, there are three steps for this matrix:
    //    Translate from origin to center,
    //    then rotate counter-clockwise by a given angles,
    //    and translate back from center to origin.
    //
    // 2. Multiply each vertex by matrix T to get its new coordinates.
    //
   

    glm::mat3x3 T1 = glm::mat3x3(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -center.x, -center.y, 1.0f);
    glm::mat3x3 T2 = glm::mat3x3(std::cos(radians), -std::sin(radians), 0.0f, std::sin(radians), std::cos(radians), 0.0f, 0.0f, 0.0f, 1.0f);
    glm::mat3x3 T3 = glm::mat3x3(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, center.x, center.y, 1.0f);

    glm::mat3 M = T3 * T2 * T1;
    
    for (int i = 0; i < grid.vertices.size(); i++) {
        glm::vec3 p = glm::vec3(grid.vertices[i].x, grid.vertices[i].y,1.0f);

        glm::vec3 p_res = M * p;

        new_grid.vertices[i] = glm::vec2(p_res.x, p_res.y);
    }

    return new_grid;
}



/// <summary>
/// Rotate an image using backward warping based on the provided meshes.
/// </summary>
/// <param name="image">input image</param>
/// <param name="src_grid">original grid</param>
/// <param name="dst_grid">rotated grid</param>
/// <param name="sampleBilinear">a function that bilinearly samples ImageFloat</param>
/// <param name="sampleBilinearRGB">a function that bilinearly samples ImageRGB</param>
/// <returns>rotated image, has the same size as input</returns>
ImageRGB rotateImage(const ImageRGB& image, const Mesh& src_grid, const Mesh& dst_grid, const BilinearSamplerFloat& sampleBilinear, const BilinearSamplerRGB& sampleBilinearRGB)
{

    //
    // Unused pixels should be black.
    // Pixel that fall outside of the image should be discarded.
   

    auto src_depth = ImageFloat(image.width, image.height);
    
    return backwardWarpImage(image, src_depth, src_grid, dst_grid, sampleBilinear, sampleBilinearRGB);
}