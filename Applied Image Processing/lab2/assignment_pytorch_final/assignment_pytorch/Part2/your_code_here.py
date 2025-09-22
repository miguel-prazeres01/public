import torch
import torch.nn.functional as Func
from helper_functions import *

def get_mask_loss(F_1, F_2, mask):
    """ Returns the mask loss.

    # Parameters:
        @F_1: torch.tensor size [1, C, H, W], the feature map of the first image
        @F_2: torch.tensor size [1, C, H, W], the feature map of the second image
        @mask: torch.tensor size [H, W], the segmentation mask. 
            NOTE: 1 encodes what areas should move and 0 what areas should stay fixed.
    
    # Returns: torch.tensor of size [1], the mask loss

    """
    _ , C, H, W = F_1.size()


    ones = torch.ones(mask.size())

    out1 = torch.sub(F_2,F_1)

    out2 = torch.sub(ones,mask)

    out_final = abs(out1 * out2)

    out_final = torch.sum(out_final)

    out_final = out_final / (C*H*W)

    return out_final
    # TODO: 4. Calculate mask loss
    #return torch.rand((1), requires_grad=True) # Initialize placeholder such that the code runs

def get_neighbourhood(p, radius):
    """ Returns a neighbourhood of points around p.

    # Parameters:
        @p: torch.tensor size [2], the current handle point p
        @radius: int, the radius of the neighbourhood to return

    # Returns: torch.tensor size [radius * radius, 2], the neighbourhood of points around p, including p
    """

    x_neighbours = torch.tensor(range(-(2*radius+1),2*radius+1), device=p.device)

    y_neighbours = torch.tensor(range(-(2*radius+1),2*radius+1), device=p.device)

    x_neighbours = torch.add(x_neighbours,p[0])

    y_neighbours = torch.add(y_neighbours,p[1])

    neighbours = torch.cartesian_prod(x_neighbours, y_neighbours)

    # TODO: 1. Get Neighbourhood
    # Note that the order of the points in the neighbourhood does not matter.
    # Do not use for-loops, make use of Pytorch functionalities.

    return neighbours

    #return torch.zeros(((2 * radius+1)**2, 2), device=p.device) # Initialize placeholder such that the code runs

def sample_p_from_feature_map(q_N, F_i):
    """ Samples the feature map F_i at the points q_N.

    # Parameters:
        @q_N: torch.tensor size [N, 2], the points to sample from the feature map
        @F_i: torch.tensor size [1, C, H, W], the feature map of the current image

    # Returns: torch.tensor size [N, C], the sampled features at q_N
    """
    assert F_i.shape[-1] == F_i.shape[-2]

    # TODO: 2. Sample features from neighbourhood
    # NOTE: As the points in q_N are floats, we can not access the points from the feature map via indexing.
    # Bilinear interpolation is needed, PyTorch has a function for this: F.grid_sample.
    # NOTE: To check whether you are using grid_sample correctly, you can pass an index matrix as the feature map F_i 
    # where each entry corresponds to its x,y index. If you sample from this feature map, you should get the same points back.   

    N , _ = q_N.size()

    _ , _ , H , W = F_i.size()

    q_new = q_N.flip(1).view(1,-1,1,2)

    q_new = torch.div(q_new,torch.Tensor([H-1,W-1])).mul(2).sub(1)

    samples = torch.nn.functional.grid_sample(F_i,q_new,align_corners=True)

    _ , C , N , _ = samples.size()

    samples = samples.view(C,N)

    samples = samples.t()

    return samples
    
    #return torch.zeros((q_N.shape[0], F_i.shape[1]), device=q_N.device) # Initialize placeholder such that the code runs

def nearest_neighbour_search(f_p, F_q_N, q_N):
    """ Does a nearest neighbourhood search in feature space to find the new handle point position.

    # Parameters:
        @f_p: torch.tensor size [1, C], the feature vector of the handle point p
        @F_q_N: torch.tensor size [N, C], the feature vectors of the neighbourhood points
        @q_N: torch.tensor size [N, 2], the neighbourhood points corresponding to the feature vectors in F_q_N

    # Returns: torch.tensor size [2], the new handle point p 
    """
    # TODO: 3. Neighbourhood search

    out = abs(torch.sub(F_q_N,f_p))

    out = out.sum(1)

    point = torch.argmin(out)

    final_point = q_N[point]

    return final_point

    #return torch.rand((2)) # Initialize placeholder such that the code runs